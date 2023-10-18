export const patchNotes: {
  readonly [version: `${number}.${number}.${number}`]: {
    readonly releaseDate: Date,
    readonly changes: readonly string[],
  }
} = {
  '2.1.0': {
    releaseDate: new Date(Date.UTC(2023, 9)),
    changes: [
      "Einführung von öffentlichen Patchnotes",
    ],
  }
};