import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegistrationRequest } from 'src/app/services/models';
import { AutehnticationService } from 'src/app/services/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerRequest: RegistrationRequest = {email: '', cognome:'', nome:'', password:'', username:''}
  errorMsg: Array<string> = [];

    constructor(
      private router: Router,
      private authService: AutehnticationService
    ){

    }


  login() {
     this.router.navigate(['login']);
  }

  register() {
    this.errorMsg = [];
    this.authService.register({
      body: this.registerRequest
    }).subscribe({
      next: () => {
        this.router.navigate(['activate-account']);
      },
      error: (err): void =>{
        this.errorMsg = err.error.validationErrors;
      }
    })
  }



}
