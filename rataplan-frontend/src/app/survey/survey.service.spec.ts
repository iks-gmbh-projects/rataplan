import { HttpClient } from "@angular/common/http";
import { Observable, of, Subject } from "rxjs";
import { SurveyHead } from "./survey.model";
import { SurveyService } from "./survey.service";


describe("SurveyService", () => {
  let httpSpy: jasmine.SpyObj<HttpClient>;
  let surveys: SurveyService;
  beforeEach(() => {
    httpSpy = jasmine.createSpyObj("HttpClient", ["get", "post", "put"]);
    surveys = new SurveyService(httpSpy);
  });

  it("should return expected surveys", (done: DoneFn) => {
    const expectedSurveys: SurveyHead[] = [
      {
        id: 0,
        name: "testA",
        description: "testA: A Description",
        startDate: new Date("10-10-2022"),
        endDate: new Date("20-10-2022"),
        openAccess: true,
        anonymousParticipation: false,
      },
      {
        id: 1,
        name: "testB",
        description: "testB: A Description",
        startDate: new Date("15-10-2022"),
        endDate: new Date("1-1-2030"),
        openAccess: false,
        anonymousParticipation: true,
      },
    ];

    httpSpy.get.and.returnValue(of(expectedSurveys));
    let count: number = 0;
    surveys.getOpenSurveys().subscribe({
      next: surveys => {
        count++;
        expect(surveys)
          .withContext("expected surveys")
          .toEqual(expectedSurveys);
      },
      error: done.fail,
      complete: () => {
        expect(count)
          .withContext("Recieved Amount")
          .toBe(1);
        done();
      },
    });
  });

  it("should return expected surveys", (done: DoneFn) => {
    const expectedSurveys: SurveyHead[] = [
      {
        id: 0,
        name: "testA",
        description: "testA: A Description",
        startDate: new Date("10-10-2022"),
        endDate: new Date("20-10-2022"),
        openAccess: true,
        anonymousParticipation: false,
      },
      {
        id: 1,
        name: "testB",
        description: "testB: A Description",
        startDate: new Date("15-10-2022"),
        endDate: new Date("1-1-2030"),
        openAccess: false,
        anonymousParticipation: true,
      },
    ];

    httpSpy.get.and.returnValue(of(expectedSurveys));
    let count: number = 0;
    surveys.getOwnSurveys().subscribe({
      next: surveys => {
        count++;
        expect(surveys)
          .withContext("expected surveys")
          .toEqual(expectedSurveys);
      },
      error: done.fail,
      complete: () => {
        expect(count)
          .withContext("Recieved Amount")
          .toBe(1);
        done();
      },
    });
  });
});