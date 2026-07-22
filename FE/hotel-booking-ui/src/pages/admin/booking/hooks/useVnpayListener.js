



import { useEffect } from 'react';
import { message } from 'antd';

/**
 * @param {() => void} onPaymentSuccess - Callback gọi khi nhận PAYMENT_SUCCESS
 */
const useVnpayListener = (onPaymentSuccess) => {
  useEffect(() => {
    const channel = new BroadcastChannel('vnpay_payment_channel');

    channel.onmessage = (event) => {
      if (event.data?.type === 'PAYMENT_SUCCESS') {
        message.success('Hệ thống đã ghi nhận thanh toán VNPay thành công.');
        if (typeof onPaymentSuccess === 'function') {
          onPaymentSuccess();
        }
      }
    };

    return () => channel.close();
  }, [onPaymentSuccess]);
};

export default useVnpayListener;
