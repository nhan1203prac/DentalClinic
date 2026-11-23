import { useEffect, useState } from 'react'
import { Alert, Button, Result, Skeleton, Steps, Typography, message } from 'antd'
import dayjs from 'dayjs'
import { useDispatch, useSelector } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { ApiError } from '../../api/client'
import { bookingApi } from '../../api/bookingApi'
import { fetchFreeSlots, fetchMyPatients } from '../../store/bookingSlice'
import { errorMessage } from '../../utils/formErrors'
import { ConfirmStep } from './ConfirmStep'
import { DetailStep } from './DetailStep'
import { PatientStep } from './PatientStep'
import { SlotStep } from './SlotStep'

const STEP_TITLES = ['Người khám', 'Thông tin khám', 'Chọn giờ', 'Xác nhận']

const emptyDraft = () => ({
  patientId: null,
  newPatient: null,
  history: [],
  reason: '',
  serviceIds: [],
  date: dayjs().format('YYYY-MM-DD'),
  scheduleId: null,
  doctorId: null,
  doctorName: null,
  time: null,
})

// Lỗi do khung giờ: quay lại bước chọn giờ để chọn lại
const SLOT_ERRORS = ['SLOT_TAKEN', 'INVALID_SLOT', 'BOOKING_OUT_OF_RANGE', 'PATIENT_TIME_CONFLICT']

// Bệnh nhân đặt lịch khám (BR-20 đến BR-25)
export function BookingPage() {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { items: patients, status, error: loadError } = useSelector((state) => state.myPatients)
  const [step, setStep] = useState(0)
  const [draft, setDraft] = useState(emptyDraft)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const [booked, setBooked] = useState(null)

  useEffect(() => {
    dispatch(fetchMyPatients())
  }, [dispatch])

  const update = (patch) => setDraft((d) => ({ ...d, ...patch }))
  const patient = patients.find((p) => p.maBenhNhan === draft.patientId) ?? null

  async function submit() {
    setSubmitting(true)
    setError(null)
    try {
      const result = await bookingApi.book({
        maBenhNhan: draft.patientId ?? undefined,
        benhNhanMoi: draft.newPatient ?? undefined,
        tienSu: draft.history,
        maLich: draft.scheduleId,
        gioHen: draft.time,
        lyDoKham: draft.reason || undefined,
        dichVuDangKy: draft.serviceIds,
      })
      setBooked(result)
      dispatch(fetchMyPatients())
    } catch (e) {
      if (e instanceof ApiError && SLOT_ERRORS.includes(e.code)) {
        message.error(e.message)
        update({ scheduleId: null, doctorId: null, doctorName: null, time: null })
        dispatch(fetchFreeSlots(draft.date))
        setStep(2)
      } else if (e instanceof ApiError && e.code === 'PATIENT_DUPLICATE') {
        message.error(e.message)
        dispatch(fetchMyPatients())
        update({ patientId: null, newPatient: null })
        setStep(0)
      } else {
        // Lỗi validate từng ô hiện thành một dòng vì các ô nằm ở nhiều bước
        setError(e instanceof ApiError && e.fields ? Object.values(e.fields).join('; ') : errorMessage(e))
      }
    } finally {
      setSubmitting(false)
    }
  }

  function restart() {
    setDraft(emptyDraft())
    setBooked(null)
    setError(null)
    setStep(0)
  }

  if (booked) {
    return (
      <Result
        status="success"
        title="Đặt lịch thành công"
        subTitle={`${booked.tenBenhNhan} khám với ${booked.tenBacSi} lúc ${booked.gioHen.slice(0, 5)} ngày ${dayjs(booked.ngay).format('DD/MM/YYYY')}`}
        extra={[
          <Button type="primary" key="list" onClick={() => navigate('/app/lich-kham')}>Xem lịch khám của tôi</Button>,
          <Button key="again" onClick={restart}>Đặt thêm lịch khác</Button>,
        ]}
      />
    )
  }

  return (
    <>
      <Typography.Title level={2}>Đặt lịch khám</Typography.Title>
      <Steps current={step} items={STEP_TITLES.map((title) => ({ title }))} responsive style={{ marginBottom: 24 }} />
      {loadError && <Alert type="error" showIcon message={loadError} style={{ marginBottom: 16 }} />}
      {status === 'idle' || status === 'loading' ? (
        <Skeleton active />
      ) : (
        <>
          {step === 0 && (
            <PatientStep
              patients={patients}
              draft={draft}
              onNext={(patch) => {
                update(patch)
                setStep(1)
              }}
            />
          )}
          {step === 1 && (
            <DetailStep
              patient={patient}
              draft={draft}
              onBack={() => setStep(0)}
              onNext={(patch) => {
                update(patch)
                setStep(2)
              }}
            />
          )}
          {step === 2 && <SlotStep draft={draft} onChange={update} onBack={() => setStep(1)} onNext={() => setStep(3)} />}
          {step === 3 && (
            <ConfirmStep draft={draft} patient={patient} submitting={submitting} error={error} onBack={() => setStep(2)} onSubmit={submit} />
          )}
        </>
      )}
    </>
  )
}
