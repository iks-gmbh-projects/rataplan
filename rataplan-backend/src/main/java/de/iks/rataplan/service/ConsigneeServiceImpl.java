package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.repository.BackendUserAccessRepository;
import de.iks.rataplan.restservice.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsigneeServiceImpl implements ConsigneeService {
    private final BackendUserAccessRepository accessRespository;
    private final AuthService authService;
    private final VoteService voteService;
    @Override
    public List<Vote> getVotesForConsignee(AuthUser authUser) {
        return accessRespository.findByInvitedIsTrueAndUserId(authUser.getId())
            .map(BackendUserAccess::getVoteId)
            .distinct()
            .map(voteService::getVoteById)
            .collect(Collectors.toList());
    }
    
    @Override
    public void transcribeConsigneesToBackendUserAccesses(Vote vote) {
        List<BackendUserAccess> ret = vote.getConsigneeList()
            .parallelStream()
            .map(authService::fetchUserIdFromEmail)
            .filter(Objects::nonNull)
            .map(userId -> new BackendUserAccess(vote.getId(), userId, false, true))
            .collect(Collectors.toList());
        voteService.addAccess(vote, ret);
    }
}
