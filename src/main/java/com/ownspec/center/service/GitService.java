package com.ownspec.center.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.ownspec.center.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.XslFilter;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import javax.annotation.PreDestroy;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;


/**
 * Created by lyrold on 15/09/2016.
 */
@Service
@Slf4j
public class GitService {

  @Value("${git.repository.path.components}")
  private String componentsGitRepositoryPath;


  private LoadingCache<String, Git> gitCache;

  public GitService() {
    this.gitCache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .removalListener(notification -> ((Git) notification.getValue()).close())
        .build(new CacheLoader<String, Git>() {
          @Override
          public Git load(String key) throws Exception {
            return Git.init().setDirectory(new File(componentsGitRepositoryPath, key)).call();
          }
        });
  }

  @PreDestroy
  public void release(){
    gitCache.invalidateAll();
  }


  public Git getGit(String id) {
    return gitCache.getUnchecked(id);
  }


  public Path getFilePath(String uid, String filename) {
    return Paths.get(componentsGitRepositoryPath, uid, filename);
  }


  public String updateAndCommit(String uid, String filename, Resource resource, User user, String message) {
    LOG.info("creating Document file [{}]", filename);

    Path filePath = getFilePath(uid, filename);

    try {
      if (!Files.exists(filePath)){
        Files.createDirectories(filePath.getParent());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


    try (OutputStream os = Files.newOutputStream(filePath); InputStream is = resource.getInputStream()) {
      IOUtils.copyLarge(is, os);
    } catch (Exception e) {
      LOG.error("An error has occurred when writing file", e);
      // TODO: 24/09/16 Create custom exception
      throw new RuntimeException(e);
    }
    return commit(uid, filename, user, message);
  }


  public void deleteAndCommit(String uid, String filename, User user, String message) {
    try {
      Files.deleteIfExists(getFilePath(uid, filename));
      commit(uid, filename, user, message);
    } catch (Exception e) {
      // TODO: 24/09/16 Create custom exception
      throw new RuntimeException(e);
    }
  }


  public String commit(String uid, String filename, User user, String message) {
    try {
      Git git = getGit(uid);

      git.add().addFilepattern(filename).call();
      RevCommit revCommit = git
          .commit().setAllowEmpty(false)
          .setAuthor(user.getUsername(), user.getEmail())
          .setOnly(filename)
          .setMessage(message).call();


      return revCommit.getId().name();

    } catch (GitAPIException e) {
      // TODO: 24/09/16 Create custom exception
      throw new RuntimeException(e);
    } catch (JGitInternalException e) {
      return null;
    }
  }


  public Iterable<RevCommit> getHistoryFor(String uid, String filename) {
    try {
      Git git = getGit(uid);

      return git.log().addPath(filename).call();
    } catch (GitAPIException e) {
      // TODO: 24/09/16 Create custom exception
      throw new RuntimeException(e);
    }
  }

  public RevCommit getLatestRevision(String uid, String filePath) {
    try {
      Git git = getGit(uid);

      return Iterables.getFirst(git.log().addPath(filePath).call(), null);
    } catch (GitAPIException e) {
      // TODO: 24/09/16 Create custom exception
      throw new RuntimeException(e);
    }
  }

  public Resource getFile(String uid, String filePath) throws IOException {
    return getFile(uid, filePath, "HEAD");
  }

  public Resource getFile(String uid, String filename, String revision) throws IOException {
    Git git = getGit(uid);

    Repository repository = git.getRepository();

    ObjectId lastCommitId = repository.resolve(revision);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    // a RevWalk allows to walk over commits based on some filtering that is defined
    try (RevWalk revWalk = new RevWalk(repository)) {
      RevCommit commit = revWalk.parseCommit(lastCommitId);
      // and using commit's tree find the path
      RevTree tree = commit.getTree();
      System.out.println("Having tree: " + tree);

      // now try to find a specific file
      try (TreeWalk treeWalk = new TreeWalk(repository)) {
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(filename));
        if (!treeWalk.next()) {
          throw new IllegalStateException("Did not find expected file 'README.md'");
        }

        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = repository.open(objectId);

        // and then one can the loader to read the file
        loader.copyTo(byteArrayOutputStream);
      }

      revWalk.dispose();
    }
    return new ByteArrayResource(byteArrayOutputStream.toByteArray());

  }


  public Resource diff(String uid, String filename, String fromRevision, String toRevision) {

    if (fromRevision == null) {
      List<RevCommit> commits = Lists.reverse(Lists.newArrayList(getHistoryFor(uid, filename)));

      for (int i = 0; i < commits.size(); i++) {
        if (commits.get(i).getId().name().equals(toRevision)) {
          if (i != 0) {
            fromRevision = commits.get(i - 1).getId().name();
            break;
          } else {
            return null;
          }
        }
      }
    }
    return doDiff(uid, filename, fromRevision, toRevision);
  }


  public Resource doDiff(String uid, String filename, String oldRevision, String newRevision) {
    try {
      Resource oldResource = getFile(uid, filename, oldRevision);
      Resource newResource = getFile(uid, filename, newRevision);
      return doDiff(oldResource, newResource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public Resource doDiff(Resource oldResource, Resource newResource) {

    InputStream oldStream = null;
    InputStream newStream = null;

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    try {

      SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

      TransformerHandler result = tf.newTransformerHandler();
      // If the file path were malformed, then the following
      result.setResult(new StreamResult(byteArrayOutputStream));


      oldStream = oldResource.getInputStream();
      newStream = newResource.getInputStream();

      XslFilter filter = new XslFilter();


      ContentHandler postProcess = filter.xsl(result, "diff/htmlheader.xsl");

      Locale locale = Locale.getDefault();
      String prefix = "diff";

      HtmlCleaner cleaner = new HtmlCleaner();

      InputSource oldSource = new InputSource(oldStream);
      InputSource newSource = new InputSource(newStream);

      DomTreeBuilder oldHandler = new DomTreeBuilder();
      cleaner.cleanAndParse(oldSource, oldHandler);
      TextNodeComparator leftComparator = new TextNodeComparator(oldHandler, locale);

      DomTreeBuilder newHandler = new DomTreeBuilder();
      cleaner.cleanAndParse(newSource, newHandler);
      TextNodeComparator rightComparator = new TextNodeComparator(newHandler, locale);

      postProcess.startDocument();
      postProcess.startElement("", "diffreport", "diffreport", new AttributesImpl());
      //doCSS(css, postProcess);
      postProcess.startElement("", "diff", "diff", new AttributesImpl());
      HtmlSaxDiffOutput output = new HtmlSaxDiffOutput(postProcess, prefix);

      HTMLDiffer differ = new HTMLDiffer(output);
      differ.diff(leftComparator, rightComparator);

      postProcess.endElement("", "diff", "diff");
      postProcess.endElement("", "diffreport", "diffreport");
      postProcess.endDocument();


    } catch (Throwable e) {

    } finally {
      try {
        if (oldStream != null) {
          oldStream.close();
        }
      } catch (IOException e) {
        //ignore this exception
      }
      try {
        if (newStream != null) {
          newStream.close();
        }
      } catch (IOException e) {
        //ignore this exception
      }
    }
    return new ByteArrayResource(byteArrayOutputStream.toByteArray());

  }

/*

  private String relativize(String filePath) {
    return git.getRepository().getWorkTree().toPath().normalize().toAbsolutePath().relativize(Paths.get(filePath).toAbsolutePath()).toString();
  }
*/


}
