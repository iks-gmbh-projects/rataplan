import { DatePipe } from '@angular/common';
import { ElementRef, Injectable } from '@angular/core';
import * as ExcelJS from 'exceljs';
import { VoteModel } from '../../models/vote.model';
import { UserVoteResults } from '../../vote/vote-results/vote-results.component';

@Injectable({
  providedIn: 'root',
})
export class ExcelService {
  
  constructor(private datePipe: DatePipe) {}
  
  createExcel(vote: VoteModel, resultsTable: ElementRef<HTMLTableElement>, userVoteResults: UserVoteResults[]) {
    const workbook: ExcelJS.Workbook = new ExcelJS.Workbook();
    workbook.addWorksheet(vote.title);
    const worksheet: ExcelJS.Worksheet = workbook.getWorksheet(vote.title)!;
    worksheet.addRow(this.mapAppointments(resultsTable, vote));
    this.createRows(worksheet, resultsTable, userVoteResults);
    workbook.xlsx.writeBuffer().then(buffer => this.saveExcelFile(buffer, `${vote.title}.xlsx`));
  }
  
  mapAppointments(resultsTable: ElementRef<HTMLTableElement>, vote: VoteModel) {
    let appointmentColumnHeaders: string[] = [`${resultsTable.nativeElement.rows[0].cells[0].textContent}\n`];
    for(let i = 0; i < vote.options.length; i++) {
      let title = `Termin ${i + 1} \r\n`;
      if(vote.options[i].startDate !== null) title = title.concat(
        'Anfangsdatum: ',
        this.datePipe.transform(new Date(vote.options[i].startDate!), 'medium')!,
        '\r\n',
      );
      if(vote.options[i].endDate !== null) title = title.concat(
        'Enddatum: ',
        this.datePipe.transform(new Date(vote.options[i].startDate!), 'medium')!,
        '\r\n',
      );
      if(vote.options[i].description !== null) title = title.concat(
        'Beschreibung: ',
        vote.options[i].description!,
        '\r\n',
      );
      appointmentColumnHeaders.push(title);
    }
    appointmentColumnHeaders.push('Letzte Aktualisierung  ');
    return appointmentColumnHeaders;
  }
  
  createRows(
    worksheet: ExcelJS.Worksheet,
    resultsTable: ElementRef<HTMLTableElement>,
    userVoteResults: UserVoteResults[],
  )
  {
    let maxColumnWidths: number[] = [];
    for(let i = 1; i < resultsTable.nativeElement.rows.length; i++) {
      let cells: HTMLCollectionOf<HTMLTableCellElement> = {...resultsTable.nativeElement.rows[i].cells};
      if(i <= userVoteResults.length) {
        let columnEntries: string[] = [
          ...Object.entries(cells).map(entry => entry[1].textContent!.toString()),
          this.datePipe.transform(userVoteResults[i-1].lastUpdated,'medium')!
        ];
        worksheet.addRow(columnEntries);
      } else worksheet.addRow([...Object.entries(cells).map(entry => entry[1].textContent!.toString())]);
      worksheet.getRow(i).eachCell((cell: ExcelJS.Cell, colNumber: number) => {
        const cellTextLength = cell.model.value!.toString().length;
        if(!maxColumnWidths[colNumber - 1] || maxColumnWidths[colNumber - 1] <
          cellTextLength) maxColumnWidths[colNumber - 1] = cellTextLength;
        this.getStyledCell(cell);
      });
    }
    for(let i = 0; i < maxColumnWidths.length; i++) worksheet.getColumn(i + 1).width = maxColumnWidths[i] + 2;
  }
  
  saveExcelFile(buffer: ArrayBuffer, filename: string): void {
    const blob = new Blob([buffer], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
    const downloadLink = document.createElement('a');
    downloadLink.href = window.URL.createObjectURL(blob);
    downloadLink.download = filename;
    downloadLink.click();
    document.removeChild(downloadLink);
  };
  
  buildSolidFill(fgColor?: Partial<ExcelJS.Color>): ExcelJS.Fill {
    return {
      type: 'pattern',
      pattern: 'solid',
      fgColor,
    };
  }
  
  getStyledCell(e: ExcelJS.Cell) {
    switch(e.model.value) {
    case 'Akzeptiert':
      e.value = 'Akzeptiert';
      e.style.fill = this.buildSolidFill({argb: '0e4823'});
      e.style.font = {...e.style.font, color: {argb: 'FFFFFFFF'}};
      break;
    case 'Vielleicht':
      e.value = 'Vielleicht';
      e.style.fill = this.buildSolidFill({argb: 'f4c42e'});
      e.style.font = {...e.style.font, color: {argb: 'FFFFFFFF'}};
      break;
    case 'Abgelehnt':
      e.value = 'Abgelehnt';
      e.style.fill = this.buildSolidFill({argb: '871c37'});
      e.style.font = {...e.style.font, color: {argb: 'FFFFFFFF'}};
      break;
    case 'Keine Antwort':
      e.value = 'Keine Antwort';
    }
  }
}