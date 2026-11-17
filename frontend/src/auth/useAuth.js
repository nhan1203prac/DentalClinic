import { useDispatch, useSelector } from 'react-redux'
import { login, logout, selectInitializing, selectUser } from '../store/authSlice'

// Trả { user, initializing, login(body), logout() }; login/logout trả Promise, lỗi là ApiError
export function useAuth() {
  const dispatch = useDispatch()
  const user = useSelector(selectUser)
  const initializing = useSelector(selectInitializing)
  return {
    user,
    initializing,
    login: (body) => dispatch(login(body)),
    logout: () => dispatch(logout()),
  }
}
