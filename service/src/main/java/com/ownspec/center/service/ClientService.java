package com.ownspec.center.service;

import com.ownspec.center.dto.ClientDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Client;
import com.ownspec.center.repository.ClientRepository;
import com.ownspec.center.repository.user.UserClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created on 19/03/2017
 *
 * @author lyrold
 */
@Service
public class ClientService {

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private UserClientRepository userClientRepository;


  public ClientDto findOneToDto(Long clientId) {
    return ClientDto.from(findOne(clientId));
  }

  public Client findOne(Long clientId) {
    return Objects.requireNonNull(clientRepository.findOne(clientId));
  }

  public List<UserDto> findClientUsers(Long clientId) {
    return userClientRepository.findAllByClientId(clientId).stream()
        .map(uc -> UserDto.fromUser(uc.getUser()))
        .collect(Collectors.toList());
  }

}
