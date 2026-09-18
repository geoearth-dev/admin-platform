const BROWSER_ID_KEY = 'admin:client-id';

export function getClientId(): string {
  let clientId = localStorage.getItem(BROWSER_ID_KEY);

  if (!clientId) {
    clientId = crypto.randomUUID();
    localStorage.setItem(BROWSER_ID_KEY, clientId);
  }

  return clientId;
}
