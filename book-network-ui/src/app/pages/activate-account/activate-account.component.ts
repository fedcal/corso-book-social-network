import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AutehnticationService } from './../../services/services/autehntication.service';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss'],
})
export class ActivateAccountComponent {


  message: string = '';
  isOkay: boolean = true;
  submitted: boolean = false;

  constructor(
    private router: Router,
    private authService: AutehnticationService
  ){

  }

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }

  confirmAccount(token: string) {
    this.authService.confirm({token}).subscribe({
      next: (): void => {
        this.message = 'Il tuo account è stato attivato con successo.\n Ora puoi procedere con il login'
        this.submitted = true;
        this.isOkay = true;
      },
      error: (): void =>{
        this.message = 'Token non valido o scaduto'
        this.submitted = true;
        this.isOkay = false;
      }
    })
  }

  redirectToLogin() {
    this.router.navigate(['login'])
  }

}
