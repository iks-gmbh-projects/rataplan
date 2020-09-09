import {
  Component, Input, Output, EventEmitter
} from '@angular/core';

@Component({
  selector: 'rp-number-input',
  templateUrl: 'number-input.component.html',
  styleUrls: ['number-input.component.css']
})
export class NumberInputComponent {
  public stringValue: string = '';
  private numberValue: number = 0;

  @Output() valueChanged = new EventEmitter();

  @Input()
  get value(): number {
    return this.numberValue;
  }

  set value(value: number) {
    this.numberValue = this.considerBounderies(value);
    this.stringValue = this.value.toString();
    this.valueChanged.emit(this.numberValue);
  }

  @Input() min: number = 0;
  @Input() max: number = 255;

  private considerBounderies(val: number): number {
    if (val > this.max) {
      return this.max;
    }

    if (val < this.min) {
      return this.min;
    }

    return val;
  }

  public setValue(stringVal: string) {
    if (stringVal === '') {
      this.stringValue = '';
      return;
    }
    this.value = parseInt(stringVal);
  }

  public increment() {
    this.value++;
  }

  public decrement() {
    this.value--;
  }

  public ckeckKey(event: any) {
    const digits = /[0-9]/;
    let c = String.fromCharCode(event.charCode);
    if (!digits.test(c)) {
      event.preventDefault();
    }
    if (parseInt(this.stringValue + c) > this.max) {
      event.preventDefault();
    }
  }

  public ckeckArrows(event: any) {
    if (event.keyCode === 38) {
      this.increment();
    } else if (event.keyCode === 40) {
      this.decrement()
    }
  }

}
