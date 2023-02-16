export type deletionMethod = "DELETE"|"ANONYMIZE";
export type deletionChoices = {
  backendChoice: deletionMethod,
  surveyToolChoice: deletionMethod,
  password: string,
};
