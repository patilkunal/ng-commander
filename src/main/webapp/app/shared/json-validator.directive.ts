import {Directive} from '@angular/core';
import {AbstractControl, Validator, NG_VALIDATORS, ValidatorFn} from '@angular/forms';

@Directive({
    selector: '[jsonValidator]',
    providers: [{provide: NG_VALIDATORS, useExisting: JsonValidatorDirective, multi: true}]
})
export class JsonValidatorDirective implements Validator {

    validate(control: AbstractControl): {[key: string]: any} {
        return control.value ? validateJSON()(control) : null;
    }
}

export function validateJSON(): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} => {
        const isvalidjson:boolean = JSON.parse(control.value) == null;
        return isvalidjson ? {'validJSON': {value: control.value}} : null;
    };
}