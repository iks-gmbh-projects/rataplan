import { NgModule } from '@angular/core';
import { AppCommonModule } from '../app-common.module';
import { VoteRoutingModule } from './vote-routing.module';
import { VoteFormComponent } from './vote-form/vote-form.component';
import { GeneralSubformComponent } from './vote-form/general-subform/general-subform.component';
import { DatepickerSubformComponent } from './vote-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './vote-form/email-subform/email-subform.component';
import { LinkSubformComponent } from './vote-form/link-subform/link-subform.component';
import { VoteComponent } from './vote/vote.component';
import { VoteDecisionSubformComponent } from './vote/member-decision-subform/vote-decision-subform.component';
import { ConfigSubformComponent } from './vote-form/config-subform/config-subform.component';
import { OverviewSubformComponent } from './vote-form/overview-subform/overview-subform.component';
import { VoteListComponent } from '../vote-list/vote-list.component';
import { StoreModule } from '@ngrx/store';
import { voteFeature } from './vote.feature';
import { EffectsModule } from '@ngrx/effects';
import { VoteEffects } from './vote.effects';
import { CombinedDatetimeMaxDirective } from '../validator/combined-datetime-max.directive';
import { CombinedDatetimeMinDirective } from '../validator/combined-datetime-min.directive';

@NgModule({
  declarations: [
    VoteFormComponent,
    GeneralSubformComponent,
    DatepickerSubformComponent,
    EmailSubformComponent,
    LinkSubformComponent,
    VoteComponent,
    VoteDecisionSubformComponent,
    ConfigSubformComponent,
    OverviewSubformComponent,
    VoteListComponent,

    CombinedDatetimeMaxDirective,
    CombinedDatetimeMinDirective,
  ],
  imports: [
    AppCommonModule,
    VoteRoutingModule,
    StoreModule.forFeature(voteFeature),
    EffectsModule.forFeature([VoteEffects]),
  ],
})
export class VoteModule {}
