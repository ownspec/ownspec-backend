package com.ownspec.center.model;

import com.ownspec.center.model.user.User;
import lombok.Data;

import java.util.Date;

/**
 * Created by lyrold on 27/09/2016.
 */
@Data
public class Revision {

    private String id;
    private User author;
    private Date date;
    private String message;
    private String currentCommitFilePath;
    private String previousCommitFilePath;
}
