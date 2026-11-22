import { useState } from 'react'
import { Alert, Form, Modal } from 'antd'
import { applyFieldErrors, errorMessage } from '../utils/formErrors'

// Modal form dùng chung: tự lo trạng thái gửi, lỗi chung và lỗi từng ô (VALIDATION_ERROR) từ backend.
// onSubmit(values) ném lỗi để giữ modal mở; onFieldError(form, error) cho phép trang xử lý mã lỗi riêng.
// Chỉ mount phần bên trong khi open, nên mỗi lần mở là form mới với initialValues mới.
export function EntityFormModal({ open, ...props }) {
  return open ? <FormModalBody {...props} /> : null
}

function FormModalBody({ title, initialValues, onCancel, onSubmit, onFieldError, children }) {
  const [form] = Form.useForm()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  async function handleOk() {
    const values = await form.validateFields().catch(() => null)
    if (!values) {
      return
    }
    setSubmitting(true)
    setError(null)
    try {
      await onSubmit(values)
    } catch (e) {
      if (!onFieldError?.(form, e) && !applyFieldErrors(form, e)) {
        setError(errorMessage(e))
      }
      setSubmitting(false)
    }
  }

  return (
    <Modal title={title} open onCancel={onCancel} onOk={handleOk} confirmLoading={submitting}
      okText="Lưu" cancelText="Hủy" maskClosable={false}>
      {error && <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} />}
      <Form form={form} layout="vertical" requiredMark={false} initialValues={initialValues}>
        {children}
      </Form>
    </Modal>
  )
}
