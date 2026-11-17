const currency = new Intl.NumberFormat('vi-VN')

export function formatVnd(amount) {
  return `${currency.format(amount)} ₫`
}
