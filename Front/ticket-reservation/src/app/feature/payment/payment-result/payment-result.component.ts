import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-payment-result',
  imports: [RouterLink],
  templateUrl: './payment-result.component.html'
})
export class PaymentResultComponent {
  private route = inject(ActivatedRoute);

  result = this.route.snapshot.paramMap.get('result') ?? 'success';
  isSuccess = computed(() => this.result === 'success');
}
