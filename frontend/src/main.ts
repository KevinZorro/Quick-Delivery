import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './app/features/edge/auth.interceptor';
import { provideAnimations } from '@angular/platform-browser/animations';

// 👇 Importa lo necesario para Google Sign-In
import {
  SocialAuthServiceConfig,
  GoogleLoginProvider
} from '@abacritt/angularx-social-login';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimations(),

    // 👇 Configuración de Google Auth (forma correcta)
    {
      provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider(
              '693559539482-8349bkuaaavsu2bj8kcd4nnf38easpg7.apps.googleusercontent.com'
            )
          }
        ],
        onError: (err: any) => console.error('Error en Google Auth:', err)
      } as SocialAuthServiceConfig,
    }
  ]
}).catch(err => console.error(err));

