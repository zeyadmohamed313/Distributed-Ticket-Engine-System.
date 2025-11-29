import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50,       
  duration: '10s', 
};

export default function () {
  const url = 'http://localhost:80/api/tickets';
  
  const randomEventId = Math.random() > 0.5 ? 101 : 102; 
  
  const seat = "Seat-" + __VU + "-" + __ITER; 

  const payload = JSON.stringify({
    eventId: randomEventId,
    seatNumber: seat,
    userId: "User-" + __VU,
    status: "Available"
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  sleep(1);
}
