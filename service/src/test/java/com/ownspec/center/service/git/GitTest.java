package com.ownspec.center.service.git;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.collect.Iterables;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;

/**
 * Created by nlabrot on 16/11/16.
 */
public class GitTest {
  Git git;

  private static final Logger LOG = LoggerFactory.getLogger(GitTest.class);

  @Before
  public void setUp() throws Exception {
    FileUtils.cleanDirectory(new File("target/foo"));
  }

  @Test
  public void name() throws Exception {

    String zPath = "target/foo/foo.txt";

    git = Git.init().setDirectory(new File("target/foo")).call();

    FileUtils.write(new File("target/foo/foo.txt") , "ok\n\n\nok\n" , UTF_8);

    git.add().addFilepattern(relativize(zPath)).call();
    git.commit().setMessage("ok1").setOnly(relativize(zPath)).setAllowEmpty(false).call();

    FileUtils.write(new File("target/foo/foo.txt") , "ok1\n\n\nok\n" , UTF_8);

    git.add().addFilepattern(relativize(zPath)).call();
    git.commit().setMessage("ok2").setOnly(relativize(zPath)).setAllowEmpty(false).call();


    Iterable<RevCommit> call = git.log().addPath(relativize(zPath)).call();

    RevCommit last = Iterables.getLast(call);

    LOG.info(last.name());

    Ref foo = git.checkout().setStartPoint(last).setName("foo").setCreateBranch(true).call();

    FileUtils.write(new File("target/foo/foo.txt") , "ok\n\n\nok2\n" , UTF_8);

    git.add().addFilepattern(relativize(zPath)).call();
    git.commit().setMessage("ok2").setOnly(relativize(zPath)).setAllowEmpty(false).call();


    git.rebase().setUpstream("master").call();

  }

  private String relativize(String filePath) {
    return git.getRepository().getWorkTree().toPath().normalize().toAbsolutePath().relativize(Paths.get(filePath).toAbsolutePath()).toString();
  }

}
