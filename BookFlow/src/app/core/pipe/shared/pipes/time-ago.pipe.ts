import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeAgo',
  standalone: true
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: string | Date | null): string {
    if (!value) return '';

    const seconds = Math.floor((new Date().getTime() - new Date(value).getTime()) / 1000);

    if (seconds < 29) {
      return 'just now';
    }

    const intervals: { [key: string]: number } = {
      year: 31536000,
      month: 2592000,
      week: 604800,
      day: 86400,
      hour: 3600,
      minute: 60,
      second: 1
    };

    let counter;
    for (const [unit, secondsInUnit] of Object.entries(intervals)) {
      counter = Math.floor(seconds / secondsInUnit);
      if (counter > 0) {
        if (counter === 1) {
          return `${counter} ${unit} ago`;
        } else {
          return `${counter} ${unit}s ago`;
        }
      }
    }

    return value.toString();  
  }
}