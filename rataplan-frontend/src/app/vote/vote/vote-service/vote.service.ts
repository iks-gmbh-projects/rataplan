import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {exhaustMap, map, Observable} from 'rxjs';

import {deserializeVoteModel, VoteModel} from '../../../models/vote.model';
import {deserializeVoteOptionDecisionModel} from '../../../models/vote-decision.model';
import {VoteParticipantModel} from '../../../models/vote-participant.model';
import {BackendUrlService} from '../../../services/backend-url-service/backend-url.service';
import {UserVoteResultResponse, UserVoteResults} from "../../vote-results/vote-results.component";

@Injectable({
    providedIn: 'root',
})
export class VoteService {

    constructor(
        private readonly http: HttpClient,
        private readonly urlService: BackendUrlService
    ) {
    }

    getVoteByParticipationToken(participationToken: string): Observable<VoteModel> {
        return this.urlService.voteBackendURL('votes', participationToken).pipe(
            exhaustMap(url => {
                return this.http.get<VoteModel<true>>(
                    url,
                    {
                        headers: new HttpHeaders({
                            'Content-Type': 'application/json;charset=utf-8',
                        }),
                    });
            }),
            map(deserializeVoteModel)
        );
    }

    addVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
        const token = this.getParticipationToken(vote)!;
        const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json'}), withCredentials: true};

        return this.urlService.voteBackendURL('votes', token, 'participants').pipe(
            exhaustMap(url => {
                return this.http.post<VoteParticipantModel<true>>(
                    url, voteParticipant, httpOptions);
            }),
            map(member => ({
                ...member,
                decisions: member.decisions.map(deserializeVoteOptionDecisionModel),
            }))
        );
    }

    updateVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<VoteParticipantModel> {
        const token = this.getParticipationToken(vote)!;

        return this.urlService.voteBackendURL('votes', token, 'participants', voteParticipant.id!).pipe(
            exhaustMap(url => {
                return this.http.put<VoteParticipantModel<true>>(
                    url,
                    voteParticipant,
                    {
                        withCredentials: true,
                        headers: new HttpHeaders({
                            'Content-Type': 'application/json;charset=utf-8',
                        }),
                    });
            }),
            map(participant => ({
                ...participant,
                decisions: participant.decisions.map(deserializeVoteOptionDecisionModel),
            }))
        );
    }

    deleteVoteParticipant(vote: VoteModel, voteParticipant: VoteParticipantModel): Observable<string> {
        const token = this.getParticipationToken(vote)!;

        return this.urlService.voteBackendURL('votes', token, 'participants', voteParticipant.id!).pipe(
            exhaustMap(url => {
                return this.http.delete(
                    url,
                    {
                        withCredentials: true,
                        responseType: 'text',
                        headers: new HttpHeaders({
                            'Content-Type': 'application/json;charset=utf-8',
                        }),
                    });
            })
        );
    }

    getParticipationToken(vote: VoteModel) {
        const token = vote.participationToken;
        if (token !== null) {
            return token;
        }
        return '' + vote.id;
    }

    getVoteResults(voteParticipationToken: string) {
        return this.urlService.voteBackendURL("vote", voteParticipationToken, "results").pipe(
            exhaustMap(url => this.http.get<UserVoteResultResponse[]>(url, {withCredentials: true}).pipe(
                    map(response => {
                        let mappedUserVoteResults: UserVoteResults[] = [];
                        for (const userVoteResultResponse of response) {
                            let mappedUserVoteResult: UserVoteResults = {username: userVoteResultResponse.username, voteOptionAnswers: new Map<number, number>()};
                            for (const mapElement of Object.entries(userVoteResultResponse.voteOptionAnswers)) {
                                mappedUserVoteResult.voteOptionAnswers.set(Number(mapElement[0]), mapElement[1])
                            }
                            mappedUserVoteResults.push(mappedUserVoteResult);
                        }
                        return mappedUserVoteResults;
                    })
                )
            ),
        )
    }
}
