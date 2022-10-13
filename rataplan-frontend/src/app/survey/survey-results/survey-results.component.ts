import { Component, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { Answer, Checkbox, Question, Survey, SurveyResponse } from '../survey.model';
import { SurveyService } from '../survey.service';

@Component({
  selector: 'app-survey-results',
  templateUrl: './survey-results.component.html',
  styleUrls: ['./survey-results.component.css']
})
export class SurveyResultsComponent implements OnInit, OnDestroy, OnChanges {
  public survey?: Survey;
  private sub?: Subscription;
  public columns: { [questionId: string | number]: string[] } = {};
  public columnNames: { [questionId: string | number]: string[] } = {};
  public answers: SurveyResponse[] = [];
  public busy: boolean = false;
  public error: any = null;

  constructor(private route: ActivatedRoute, private surveys: SurveyService) { }

  public ngOnInit(): void {
    this.fetchAnswers(this.route.snapshot.data['survey']);
    this.sub = this.route.data.subscribe(d => this.fetchAnswers(d['survey']));
  }

  public ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  public ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  public checkboxChecked(response: SurveyResponse, questionId: string | number, checkboxId: string | number): boolean {
    return (response.answers[questionId].checkboxes || {})[checkboxId];
  }

  private fetchAnswers(survey: Survey): void {
    if (this.survey === survey) return;
    this.busy = true;
    this.survey = survey;
    this.columns = {};
    this.columnNames = {};
    for (let questionGroup of survey.questionGroups) {
      for (let question of questionGroup.questions) {
        if (question.id) {
          this.columns[question.id] = ["user"];
          this.columnNames[question.id] = ["Nutzer"];
          let hasTextfield: boolean = false;
          if (question.checkboxGroup) {
            for (let checkbox of question.checkboxGroup.checkboxes) {
              if (checkbox.id) {
                this.columns[question.id].push("checkbox" + checkbox.id);
                this.columnNames[question.id].push("\"" + checkbox.text.replace(/"/, "\"") + "\"");
              }
              if (checkbox.hasTextField) hasTextfield = true;
            }
          } else hasTextfield = true;
          if (hasTextfield) {
            this.columns[question.id].push("answer");
            this.columnNames[question.id].push("Antwort");
          }
        }
      }
    }
    this.answers = [];
    this.error = null;
    this.surveys.fetchAnswers(survey).subscribe({
      next: answers => this.answers = answers,
      error: err => {
        this.error = err;
        this.busy = false;
      },
      complete: () => this.busy = false,
    })
  }

  public hasTextfield(question: Question): boolean {
    if (!question.checkboxGroup) return true;
    for (let checkbox of question.checkboxGroup.checkboxes) {
      if (checkbox.hasTextField) return true;
    }
    return false;
  }

  public toCheckbox(checked: boolean): string {
    return checked ? "check_box" : "check_box_outline_blank";
  }

  public checkboxPercentage(questionId: string | number, checkboxId: string | number): number {
    let count = 0;
    let total = 0;
    for (let response of this.answers) {
      let answer = response.answers[questionId];
      if (answer) {
        total++;
        if (answer.checkboxes![checkboxId]) count++;
      }
    }
    return count * 100 / total;
  }

  private compileResults(question: Question): string[] | null {
    if (!question.id) return null;
    const questionId = question.id;
    return [
      this.columnNames[questionId].join(", "),
      ...this.answers.map(answer => {
        const ret = [
          answer.userId || "Anonym",
          ...this.columns[questionId].filter(col => col.startsWith("checkbox")).map(col => !!answer.answers[questionId].checkboxes![col.substring(8)]),
          answer.answers[questionId].text?.replace(/"/, "\"\"")?.replace(/^|$/, "\"") || "",
        ];
        return ret.join(", ");
      })
    ];
  }

  public downloadResults(): void {
    if (!this.survey) return;
    let lines = ["", ...this.answers.map(() => "")];
    for (let group of this.survey?.questionGroups) {
      for (let question of group.questions) {
        if (question.id) {
          const compiledResults = this.compileResults(question);
          if (compiledResults) lines = lines.map((s, i) => (s ? s + ", " : "") + compiledResults[i]);
        }
      }
    }
    const blob = new Blob(lines.map(l => l + "\n"), {
      type: "text/csv",
      endings: "native",
    });
    const url = URL.createObjectURL(blob);
    const element = document.createElement("a");
    element.href = url;
    element.click();
    element.remove();
    URL.revokeObjectURL(url);
  }
}
