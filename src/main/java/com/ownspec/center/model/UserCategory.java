package com.ownspec.center.model;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * Created by lyrold on 25/09/2016.
 */
@Data
@Embeddable
public class UserCategory {
    private String value;

}
