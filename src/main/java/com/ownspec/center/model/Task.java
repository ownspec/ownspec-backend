package com.ownspec.center.model;

import com.ownspec.center.model.user.User;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.Date;

/**
 * Created by lyrold on 02/10/2016.
 */
@Data
@Entity
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private String description;

    private Double progress;

    @CreatedDate
    private Instant createdDate;

    private Date deadline;

    @ManyToOne
    private User owner;
}
