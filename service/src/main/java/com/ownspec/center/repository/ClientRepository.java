package com.ownspec.center.repository;

import com.ownspec.center.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 19/03/2017
 *
 * @author lyrold
 */
public interface ClientRepository extends JpaRepository<Client, Long> {

}
