export type deletionMethod = "DELETE"|"ANONYMIZE";
export type DeletionChoices = {
  backendChoice: deletionMethod,
  surveyToolChoice: deletionMethod,
  password: string,
};