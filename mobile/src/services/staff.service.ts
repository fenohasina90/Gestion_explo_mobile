import apiService from './api.service';
import type {
  Staff,
  RoleStaff,
  CreateStaffRequest,
  UpdateStaffRequest,
} from '@/types';

/**
 * Service pour la gestion des staffs
 */
class StaffService {
  private readonly baseUrl = '/api/staff';

  /**
   * Créer un nouveau staff
   */
  async createStaff(data: CreateStaffRequest): Promise<Staff> {
    return apiService.post<Staff>(this.baseUrl, data);
  }

  /**
   * Mettre à jour un staff
   */
  async updateStaff(id: number, data: UpdateStaffRequest): Promise<Staff> {
    return apiService.put<Staff>(`${this.baseUrl}/${id}`, data);
  }

  /**
   * Supprimer un staff
   */
  async deleteStaff(id: number): Promise<void> {
    return apiService.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer tous les staffs
   */
  async getAllStaffs(): Promise<Staff[]> {
    return apiService.get<Staff[]>(this.baseUrl);
  }

  /**
   * Récupérer les staffs avec filtres
   */
  async getStaffsWithFilters(anneeExerciceId?: number, roleId?: number): Promise<Staff[]> {
    const params: any = {};
    if (anneeExerciceId) params.anneeExerciceId = anneeExerciceId;
    if (roleId) params.roleId = roleId;
    
    return apiService.get<Staff[]>(`${this.baseUrl}/filter`, { params });
  }

  /**
   * Récupérer un staff par ID
   */
  async getStaffById(id: number): Promise<Staff> {
    return apiService.get<Staff>(`${this.baseUrl}/${id}`);
  }

  /**
   * Récupérer tous les rôles de staff
   */
  async getAllRoles(): Promise<RoleStaff[]> {
    return apiService.get<RoleStaff[]>('/api/roles');
  }
}

export default new StaffService();
