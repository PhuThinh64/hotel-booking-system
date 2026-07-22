import { useMemo } from 'react';
import {
  getServiceQuantity,
  getServiceAmount,
  getTotalInvoiceAmount,
  getRemainingAmount,
  getBookingNights,
} from '../utils/bookingHelpers';

export const useBookingCalculations = (booking) => {
  return useMemo(() => {
    if (!booking) {
      return {
        serviceQuantity: 0,
        serviceAmount: 0,
        totalInvoiceAmount: 0,
        remainingAmount: 0,
        bookingNights: 0,
      };
    }
    return {
      serviceQuantity: getServiceQuantity(booking),
      serviceAmount: getServiceAmount(booking),
      totalInvoiceAmount: getTotalInvoiceAmount(booking),
      remainingAmount: getRemainingAmount(booking),
      bookingNights: getBookingNights(booking),
    };
  }, [booking]);
};

export default useBookingCalculations;
