import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import inscriptionService from '../services/inscription.service';
import enfantService from '../services/enfant.service';
import parentService from '../services/parent.service';
import classeService from '../services/classe.service';
import anneeExerciceService from '../services/annee-exercice.service';
import type {
  InscriptionResponse,
  EnfantResponse,
  EnfantSuggestion,
  Parent,
  ParentSuggestion,
  CreateEnfantRequest,
  CreateParentRequest,
  CreateInscriptionRequest,
  Classe,
  AnneeExercice,
} from '../types';
import './EnfantsPage.css';

type TabType = 'enfants' | 'parents';

export const EnfantsPage = () => {
  const { user: currentUser } = useAuth();
  const [activeTab, setActiveTab] = useState<TabType>('enfants');
  
  // États communs
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [classes, setClasses] = useState<Classe[]>([]);
  const [anneesExercice, setAnneesExercice] = useState<AnneeExercice[]>([]);
  
  // États pour les inscriptions (onglet Enfants)
  const [inscriptions, setInscriptions] = useState<InscriptionResponse[]>([]);
  const [filteredInscriptions, setFilteredInscriptions] = useState<InscriptionResponse[]>([]);
  const [filterAnneeId, setFilterAnneeId] = useState<number | null>(null);
  const [filterClasseId, setFilterClasseId] = useState<number | null>(null);
  const [filterGenre, setFilterGenre] = useState<string>('');
  
  // États pour les parents (onglet Parents)
  const [parents, setParents] = useState<Parent[]>([]);
  const [filteredParents, setFilteredParents] = useState<Parent[]>([]);
  const [parentEnfants, setParentEnfants] = useState<Record<number, EnfantResponse[]>>({});
  const [filterParentAnneeId, setFilterParentAnneeId] = useState<number | null>(null);
  const [filterParentClasseId, setFilterParentClasseId] = useState<number | null>(null);
  
  // États pour les modals
  const [showInscriptionModal, setShowInscriptionModal] = useState(false);
  const [showParentModal, setShowParentModal] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [selectedInscription, setSelectedInscription] = useState<InscriptionResponse | null>(null);
  
  // États pour l'auto-complétion enfant
  const [enfantSearchQuery, setEnfantSearchQuery] = useState('');
  const [enfantSuggestions, setEnfantSuggestions] = useState<EnfantSuggestion[]>([]);
  const [selectedEnfant, setSelectedEnfant] = useState<EnfantSuggestion | null>(null);
  const [showEnfantForm, setShowEnfantForm] = useState(false);
  
  // États pour l'auto-complétion parent
  const [parentSearchQuery, setParentSearchQuery] = useState('');
  const [parentSuggestions, setParentSuggestions] = useState<ParentSuggestion[]>([]);
  const [selectedParent, setSelectedParent] = useState<ParentSuggestion | null>(null);
  const [showParentForm, setShowParentForm] = useState(false);
  
  // États pour les formulaires
  const [inscriptionFormData, setInscriptionFormData] = useState<CreateInscriptionRequest>({
    enfantId: 0,
    anneeExerciceId: 0,
    classeId: 0,
    estAssurance: false,
  });
  
  const [enfantFormData, setEnfantFormData] = useState<CreateEnfantRequest>({
    nom: '',
    prenom: '',
    genre: 'GARCON',
    dateNaissance: '',
    adresse: '',
    parentId: 0,
    bapteme: '',
  });
  
  const [parentFormData, setParentFormData] = useState<CreateParentRequest>({
    nom: '',
    prenom: '',
    adresse: '',
    telephone: '',
  });
  
  useEffect(() => {
    loadData();
  }, []);
  
  useEffect(() => {
    applyFilters();
  }, [inscriptions, filterAnneeId, filterClasseId, filterGenre]);
  
  useEffect(() => {
    applyParentFilters();
  }, [parents, parentEnfants, filterParentAnneeId, filterParentClasseId]);
  
  useEffect(() => {
    // Auto-complétion enfant avec debounce
    const timer = setTimeout(() => {
      if (enfantSearchQuery.length >= 1 && inscriptionFormData.anneeExerciceId) {
        searchEnfants();
      } else {
        setEnfantSuggestions([]);
        setShowEnfantForm(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [enfantSearchQuery, inscriptionFormData.anneeExerciceId]);
  
  useEffect(() => {
    // Auto-complétion parent avec debounce
    const timer = setTimeout(() => {
      if (parentSearchQuery.length >= 1) {
        searchParents();
      } else {
        setParentSuggestions([]);
        setShowParentForm(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [parentSearchQuery]);
  
  const loadData = async () => {
    try {
      setLoading(true);
      const [inscriptionsData, parentsData, classesData, anneesData] = await Promise.all([
        inscriptionService.getAllInscriptions(),
        parentService.getAllParents(),
        classeService.getAllClasses(),
        anneeExerciceService.getAllAnneesExercice(),
      ]);
      
      setInscriptions(inscriptionsData);
      setParents(parentsData);
      setClasses(classesData);
      setAnneesExercice(anneesData);
      
      // Charger les enfants de chaque parent
      const enfantsMap: Record<number, EnfantResponse[]> = {};
      await Promise.all(
        parentsData.map(async (parent) => {
          const enfants = await enfantService.getEnfantsByParentId(parent.id);
          enfantsMap[parent.id] = enfants;
        })
      );
      setParentEnfants(enfantsMap);
      
      setError(null);
    } catch (err: any) {
      console.error('Erreur lors du chargement des données:', err);
      setError(err.response?.data?.message || 'Erreur lors du chargement des données');
    } finally {
      setLoading(false);
    }
  }
