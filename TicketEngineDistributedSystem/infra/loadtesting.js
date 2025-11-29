import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 50,       // 50 مستخدم في نفس الوقت
  duration: '10s', // مدة المعركة
};

export default function () {
  // 👇 التعديل: بنضرب في Nginx (Port 80) عشان يوزع الحمل
  const url = 'http://localhost:80/api/tickets';
  
  // اختيار عشوائي (Sharding Test)
  const randomEventId = Math.random() > 0.5 ? 101 : 102; 
  
  // مقعد مختلف لكل مستخدم عشان نشوف سرعة الحجز
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
