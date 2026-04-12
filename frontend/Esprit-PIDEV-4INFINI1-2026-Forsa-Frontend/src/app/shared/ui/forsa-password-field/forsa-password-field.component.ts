import { booleanAttribute, Component, forwardRef, Input, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ForsaIconComponent } from '../forsa-icon/forsa-icon.component';
import { ForsaInputDirective } from '../../directives/forsa-input.directive';

@Component({
  selector: 'app-forsa-password-field',
  standalone: true,
  imports: [FormsModule, ForsaInputDirective, ForsaIconComponent],
  templateUrl: './forsa-password-field.component.html',
  styleUrl: './forsa-password-field.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ForsaPasswordFieldComponent),
      multi: true,
    },
  ],
})
export class ForsaPasswordFieldComponent implements ControlValueAccessor {
  @Input({ transform: booleanAttribute }) required = false;
  @Input() inputId = 'password';
  @Input() name = 'password';
  @Input() placeholder = '';
  /** Visible label text; associated to the input with for/id (satisfies tools that only scan this template). */
  @Input() labelText = 'Password';
  @Input() autocomplete = 'current-password';
  @Input() inputClass = 'input-h-12';

  protected value = '';
  protected isDisabled = false;
  protected readonly visible = signal(false);

  #onChange: (value: string) => void = () => {};
  #onTouched: () => void = () => {};

  writeValue(value: string | null): void {
    this.value = value ?? '';
  }

  registerOnChange(fn: (value: string) => void): void {
    this.#onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.#onTouched = fn;
  }

  setDisabledState(disabled: boolean): void {
    this.isDisabled = disabled;
  }

  protected onInput(ev: Event): void {
    const v = (ev.target as HTMLInputElement).value;
    this.value = v;
    this.#onChange(v);
  }

  protected onBlur(): void {
    this.#onTouched();
  }

  protected toggleVisibility(): void {
    this.visible.update((v) => !v);
  }
}
