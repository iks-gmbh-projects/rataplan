package de.iks.rataplan.controller;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.dto.VoteDTO;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.ConsigneeService;
import de.iks.rataplan.service.VoteService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VoteConsigneeControllerService {
    private final AuthService authService;
    private final ConsigneeService consigneeService;
    private final VoteService voteService;
    private final ModelMapper modelMapper;
    public List<VoteDTO> getVotesWhereUserIsConsignee(Jwt token) {
        AuthUser user = authService.getUserData(token);
        if(user == null) return Collections.emptyList();
        List<Vote> votes = consigneeService.getVotesForConsignee(user);
        List<Vote> participations = voteService.getVotesWhereUserParticipates(user.getId());
        votes.removeIf(v -> participations.stream().anyMatch(v2 -> Objects.equals(v.getId(), v2.getId())));
        return modelMapper.map(votes, new TypeToken<List<VoteDTO>>() {}.getType());
    }
}