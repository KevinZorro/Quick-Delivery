import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError, map, debounceTime, switchMap } from 'rxjs/operators';
import { AuthService } from '../auth.service';


export function correoExistenteValidator(authService: AuthService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    if (!control.value) {
      return of(null);
    }

    return of(control.value).pipe(
      debounceTime(500), // espera a que el usuario deje de escribir
      switchMap(correo =>
        authService.verificarCorreo(correo).pipe(
          map((existe: boolean) => (existe ? { correoExistente: true } : null)),
          catchError(() => of(null))
        )
      )
    );
  };
}
