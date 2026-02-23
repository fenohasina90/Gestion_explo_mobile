package com.explorateur.backend.aspect;

import com.explorateur.backend.service.JournalService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Aspect AOP pour l'audit automatique des actions
 * Intercepte les méthodes des services et enregistre les actions dans le journal
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final JournalService journalService;

    /**
     * Pointcut pour toutes les méthodes des services SAUF JournalService
     */
    @Pointcut("execution(* com.explorateur.backend.service.*Service.*(..)) && !execution(* com.explorateur.backend.service.JournalService.*(..))")
    public void serviceMethodsPointcut() {}

    /**
     * Pointcut pour les méthodes de création
     */
    @Pointcut("execution(* com.explorateur.backend.service.*Service.create*(..)) && !execution(* com.explorateur.backend.service.JournalService.*(..))")
    public void createMethodsPointcut() {}

    /**
     * Pointcut pour les méthodes de mise à jour
     */
    @Pointcut("execution(* com.explorateur.backend.service.*Service.update*(..)) && !execution(* com.explorateur.backend.service.JournalService.*(..))")
    public void updateMethodsPointcut() {}

    /**
     * Pointcut pour les méthodes de suppression
     */
    @Pointcut("execution(* com.explorateur.backend.service.*Service.delete*(..)) && !execution(* com.explorateur.backend.service.JournalService.*(..))")
    public void deleteMethodsPointcut() {}

    /**
     * Pointcut pour les méthodes d'activation/désactivation
     */
    @Pointcut("execution(* com.explorateur.backend.service.*Service.toggle*(..)) && !execution(* com.explorateur.backend.service.JournalService.*(..))")
    public void toggleMethodsPointcut() {}

    /**
     * Audit après création
     */
    @AfterReturning(pointcut = "createMethodsPointcut()", returning = "result")
    public void auditCreate(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            
            // Extraire le nom de l'entité depuis le nom du service
            String entityName = className.replace("Service", "");
            
            String action = String.format("Création - %s créé via %s", entityName, methodName);
            journalService.logAction(action);
        } catch (Exception e) {
            // Log silencieux en cas d'erreur
        }
    }

    /**
     * Audit après mise à jour
     */
    @AfterReturning(pointcut = "updateMethodsPointcut()", returning = "result")
    public void auditUpdate(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String entityName = className.replace("Service", "");
            
            // Extraire l'ID du premier argument si disponible
            Object[] args = joinPoint.getArgs();
            String idInfo = args.length > 0 ? " (ID: " + args[0] + ")" : "";
            
            String action = String.format("Modification - %s modifié%s via %s", entityName, idInfo, methodName);
            journalService.logAction(action);
        } catch (Exception e) {
            // Log silencieux en cas d'erreur
        }
    }

    /**
     * Audit après suppression
     */
    @AfterReturning(pointcut = "deleteMethodsPointcut()")
    public void auditDelete(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String entityName = className.replace("Service", "");
            
            Object[] args = joinPoint.getArgs();
            String idInfo = args.length > 0 ? " (ID: " + args[0] + ")" : "";
            
            String action = String.format("Suppression - %s supprimé%s via %s", entityName, idInfo, methodName);
            journalService.logAction(action);
        } catch (Exception e) {
            // Log silencieux en cas d'erreur
        }
    }

    /**
     * Audit après activation/désactivation
     */
    @AfterReturning(pointcut = "toggleMethodsPointcut()")
    public void auditToggle(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String entityName = className.replace("Service", "");
            
            Object[] args = joinPoint.getArgs();
            String idInfo = args.length > 0 ? " (ID: " + args[0] + ")" : "";
            
            String action = String.format("Activation/Désactivation - %s modifié%s via %s", entityName, idInfo, methodName);
            journalService.logAction(action);
        } catch (Exception e) {
            // Log silencieux en cas d'erreur
        }
    }
}
