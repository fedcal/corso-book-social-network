import { AutehnticationService } from './../../services/services/autehntication.service';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationRequest, AuthenticationResponse } from 'src/app/services/models';
import { TokenService } from 'src/app/services/token/token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  authRequest: AuthenticationRequest = {email: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AutehnticationService,
    private tokenService: TokenService
  ){

  }

  login() {
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res: AuthenticationResponse): void =>{
        this.tokenService.token = res.token as string;
        this.router.navigate(['books']);
      },
      error: (err):void =>{
        console.log(err);
        if(err.error.validationErrors){
          this.errorMsg = err.error.validationErrors
        } else {
          this.errorMsg.push(err.error.error);
        }
      }
    });
  }

  register() {
    this.router.navigate(['register'])
  }




}
