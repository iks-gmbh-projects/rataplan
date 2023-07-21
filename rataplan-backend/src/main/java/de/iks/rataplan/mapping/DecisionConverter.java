package de.iks.rataplan.mapping;

import de.iks.rataplan.domain.Decision;
import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteOption;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.repository.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecisionConverter {
	private final VoteOptionRepository voteOptionRepository;

	public final Converter<VoteDecision, VoteDecisionDTO> toDTO = new AbstractConverter<>() {
        @Override
        protected VoteDecisionDTO convert(VoteDecision voteDecision) {
            VoteDecisionDTO dtoDecision = new VoteDecisionDTO();
            dtoDecision.setOptionId(voteDecision.getVoteOption().getId());
            dtoDecision.setParticipantId(voteDecision.getVoteParticipant().getId());
            
            if (voteDecision.getDecision() != null) {
                dtoDecision.setDecision(voteDecision.getDecision().getValue());
            } else if (voteDecision.getParticipants() != null) {
                dtoDecision.setParticipants(voteDecision.getParticipants());
            }
            return dtoDecision;
        }
    };

	public final Converter<VoteDecisionDTO, VoteDecision> toDAO = new AbstractConverter<>() {
        @Override
        protected VoteDecision convert(VoteDecisionDTO dtoDecision) {
            VoteDecision decision = new VoteDecision();
            VoteOption voteOption = voteOptionRepository.findById(dtoDecision.getOptionId()).orElse(null);
            decision.setVoteOption(voteOption);
            
            if (dtoDecision.getDecision() != null) {
                decision.setDecision(Decision.getDecisionById(dtoDecision.getDecision()));
            } else if (dtoDecision.getParticipants() != null) {
                decision.setParticipants(dtoDecision.getParticipants());
            }
            return decision;
        }
    };
}