import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from '@angular/router';
import {combineLatest, forkJoin, Observable} from 'rxjs';

import {VoteModel} from '../../../models/vote.model';
import {VoteService} from '../vote-service/vote.service';
import {map} from "rxjs/operators";
import {UserVoteResults} from "../../vote-results/vote-results.component";

@Injectable({
    providedIn: 'root',
})
export class VoteResultsResolver implements Resolve<{
    vote: VoteModel,
    results: UserVoteResults[]
}> {

    constructor(
        private router: Router,
        private VoteService: VoteService,
    ) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<{ vote: VoteModel, results: UserVoteResults[] }> | Promise<{ vote: VoteModel, results: UserVoteResults[] }> | { vote: VoteModel, results: UserVoteResults[] } {
        const vote = this.VoteService.getVoteByParticipationToken(route.params['id']);
        const voteResults = this.VoteService.getVoteResults(route.params['id']);
        return combineLatest([vote, voteResults]).pipe(
            map(
                ([voteData, voteResultsData]) => {
                    return {vote: voteData, results: voteResultsData}
                })
        );


        // return combineLatest(vote,voteResults).pipe(
        //     map(([vote,voteResults]) => {
        //         return {vote:vote,voteResults:voteResults}
        // })
        // )
    }
}
