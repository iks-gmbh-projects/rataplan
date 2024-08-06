import { NgModule } from '@angular/core';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { NgChartsModule } from 'ng2-charts';
import { AppCommonModule } from '../app-common.module';
import { ExcelService } from '../services/excel-service/excel-service';
import { VoteListComponent } from '../vote-list/vote-list.component';
import { ConfigSubformComponent } from './vote-form/config-subform/config-subform.component';
import { DatepickerSubformComponent } from './vote-form/datepicker-subform/datepicker-subform.component';
import { EmailSubformComponent } from './vote-form/email-subform/email-subform.component';
import { GeneralSubformComponent } from './vote-form/general-subform/general-subform.component';
import { LinkSubformComponent } from './vote-form/link-subform/link-subform.component';
import { OverviewSubformComponent } from './vote-form/overview-subform/overview-subform.component';
import { VoteFormComponent } from './vote-form/vote-form.component';
import { VoteOptionInfoDialogComponent } from './vote-results/vote-option-info-dialog/vote-option-info-dialog.component';
import { VoteResultsComponent } from './vote-results/vote-results.component';
import { VoteRoutingModule } from './vote-routing.module';
import { VoteEffects } from './vote.effects';
import { voteFeature } from './vote.feature';
import { VoteDecisionSubformComponent } from './vote/member-decision-subform/vote-decision-subform.component';
import { VoteComponent } from './vote/vote.component';

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
    
    VoteResultsComponent,
    VoteOptionInfoDialogComponent,
  ],
  imports: [
    AppCommonModule,
    VoteRoutingModule,
    StoreModule.forFeature(voteFeature),
    EffectsModule.forFeature([VoteEffects]),
    MatSelectModule,
    MatMenuModule,
    NgChartsModule
  ],
  providers: [
    ExcelService,
  ]
})
export class VoteModule {}