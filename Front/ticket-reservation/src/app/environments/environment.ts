export const environment = {
  production: true,
  useMockData: false, // production should hit the real backend, not mock data
  // dev: empty = same-origin, the ng dev-server proxy forwards /auth and /api/* to the services.
  // For a deployed build set this to the gateway URL (note: gateway currently routes /api/auth/**, not /auth/**)
  apiUrl: '',
  googleClientId: '322111702009-bkc62icl9bob1fr42ef9imk1362cbve4.apps.googleusercontent.com',
  reservationUrl: '/api/reservations',
  showUrl: '/api',
<<<<<<< Updated upstream
  movieUrl: '/api',
  aiUrl: '/api/ai'
=======
  userUrl: 'http://localhost:8090/api',
  movieUrl: '/api'
>>>>>>> Stashed changes
};
