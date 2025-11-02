// auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  console.log('🌐 Interceptor ejecutado para:', req.url);
  
  // Obtener el token del localStorage
  const token = localStorage.getItem('quick-delivery-token');
  
  console.log('🔍 Token existe:', token ? 'SÍ' : 'NO');

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    console.log('🔑 Token agregado al header');
    console.log('📄 Token (primeros 50 chars):', token.substring(0, 50) + '...');
  } else {
    console.warn('⚠️ NO hay token en localStorage');
    console.warn('📦 Claves en localStorage:', Object.keys(localStorage));
  }

  return next(req);
};
