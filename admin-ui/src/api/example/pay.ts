import { requestClient } from "@/utils/request";

export function pay(data) {
  return requestClient.post('/payment/pay', data);
}
