package saltlux.ctv.tranSS.repository.po;

import org.apache.commons.beanutils.BeanUtils;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.payload.po.PoFilterRequest;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.security.UserPrincipal;
import saltlux.ctv.tranSS.util.AuthUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static saltlux.ctv.tranSS.util.TransformUtil.nextDay;

public class PORepositoryImpl implements PORepositoryCustom {
    @PersistenceContext
    private
    EntityManager em;

    @Override
    public PagedResponse<PoProjectAssignmentResponse> search(int page, int size, String keyWord, String orderBy,
                                                             String sortDirection,
                                                             PoFilterRequest filters,
                                                             String pmVtcCode,
                                                             UserPrincipal currentUser) {

        Query query = em.createNativeQuery(buildGetListQuery(filters, pmVtcCode, false, currentUser));
        Query countQuery = em.createNativeQuery(buildGetListQuery(filters, pmVtcCode, true, currentUser));
        if (null != pmVtcCode && !currentUser.isAdmin() && !AuthUtil.isPmLeader(currentUser)) {
            query.setParameter("pmid", pmVtcCode);
            countQuery.setParameter("pmid", pmVtcCode);
        }
        query.setMaxResults(size);
        query.setFirstResult(page);
        List<Object[]> assignments = query.getResultList();

        int total = Integer.parseInt(countQuery.getSingleResult().toString());
        int pageCount = total / size + 1;

        return new PagedResponse<>(convertToDto(assignments), page, size, total, pageCount, false);
    }

    @Override
    public List<PoProjectAssignmentResponse> getAllForInvoice(String company,
                                                              String candidateCode, String externalResourceName,
                                                              boolean checkInvoiceIsNull) throws ParseException {
        Query query = em.createNativeQuery(buildInvoiceFilterQuery(company, candidateCode, externalResourceName, checkInvoiceIsNull));

        query.setParameter("finished_at", nextDay());
        if (null != company) {
            query.setParameter("company", company);
        }
        if (null != candidateCode) {
            query.setParameter("candidateCode", candidateCode);
        } else if (null != externalResourceName) {
            query.setParameter("external_resource_name", externalResourceName);
        }
        List<Object[]> assignments = query.getResultList();
        return convertToDto(assignments);
    }

    @Override
    public void updateInvoiceId(Long invoiceId) {
        Query query = em.createNativeQuery("UPDATE purchase_order po SET po.invoice_id = NULL WHERE po.invoice_id=:invoiceId");
        query.setParameter("invoiceId", invoiceId);
        query.executeUpdate();
    }

    private String buildGetListQuery(PoFilterRequest filters, String pmVtcCode,
                                     boolean isCountQuery, UserPrincipal currentUser) {
        StringBuilder builder = new StringBuilder();
        if (isCountQuery) {
            builder.append("SELECT COUNT(*)");
        } else {
            builder.append("SELECT a.id, p.id AS projectId, po.code as poNo, p.code AS projectCode, c.code as resourceCode, concat_ws(IFNULL(a.external_resource_name, ''),'', IFNULL(c.name, '')) as resourceName, a.task, a.source, a.target, a.unit_price, a.ho, a.hb, a.total, po.currency, a.net_or_hour, a.reprep, a.rep100, a.rep99_95, a.rep94_85, a.rep84_75,a.repno_match,a.total_rep,a.wrep,a.w100, a.w99_95,a.w94_85, a.w84_75, a.wno_match, a.created_at, a.updated_at, i.id as invoiceId, p.company ");
        }
        builder.append("FROM project_assignment  a LEFT JOIN projects p ON  a.project_id = p.id LEFT JOIN candidates c on a.candidate_id = c.id LEFT JOIN purchase_order po ON a.id = po.assignment_id LEFT JOIN invoices i ON i.id = po.invoice_id");
        builder.append(" WHERE 1=1 ");
        if (null != pmVtcCode && !currentUser.isAdmin() && !AuthUtil.isPmLeader(currentUser)) { //NOT ADMIN AND PM LEADER
            builder.append(" AND p.pm_vtc = :pmid ORDER BY p.code");
        }
        String filterQuery = buildFilterCondition(filters);
        if (null != filterQuery) {
            builder.append(filterQuery);
        }
        return builder.toString();
    }

    /**
     * @param company
     * @param candidateCode resourceCode
     * @return String
     */
    private String buildInvoiceFilterQuery(String company, String candidateCode, String externalResourceName, boolean checkInvoiceIsNull) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT a.id, p.id AS projectId, po.code as poNo, p.code AS projectCode, c.code as resourceCode, concat_ws(IFNULL(a.external_resource_name, ''),'', IFNULL(c.name, '')) as resourceName, a.task, a.source, a.target, a.unit_price, a.ho, a.hb, a.total, po.currency, a.net_or_hour, a.reprep, a.rep100, a.rep99_95, a.rep94_85, a.rep84_75,a.repno_match,a.total_rep,a.wrep,a.w100, a.w99_95,a.w94_85, a.w84_75, a.wno_match, a.created_at, a.updated_at, i.id as invoiceId, p.company ");
        builder.append("FROM project_assignment  a LEFT JOIN projects p ON  a.project_id = p.id LEFT JOIN candidates c on a.candidate_id = c.id LEFT JOIN purchase_order po ON a.id = po.assignment_id LEFT JOIN invoices i ON i.id = po.invoice_id");
        builder.append(" WHERE po.code IS NOT NULL AND a.progress = 'FINISHED' AND i.is_confirmed IS NULL AND a.finished_at <= :finished_at");
        if (null != company) {
            builder.append(" AND p.company=:company ");
        }
        if (checkInvoiceIsNull) {
            builder.append(" AND i.id IS NULL ");
        }
        if (null != candidateCode) {
            builder.append(" AND c.code=:candidateCode");
        } else if (null != externalResourceName) {
            builder.append(" AND a.external_resource_name=:external_resource_name");
        }
        builder.append(" ORDER BY resourceName");

        return builder.toString();
    }

    private List<PoProjectAssignmentResponse> convertToDto(List<Object[]> assignments) {
        List<PoProjectAssignmentResponse> list = new ArrayList<>();
        Map<String, Integer> mapFields = getMapFields();
        for (Object[] a : assignments) {
            PoProjectAssignmentResponse summary = new PoProjectAssignmentResponse();
            mapFields.forEach((field, index) -> {
                try {
                    if (index <= 31) {// number of cols
                        if (a[index] instanceof Timestamp) {
                            Timestamp timestamp = (Timestamp) a[index];
                            BeanUtils.setProperty(summary, field, timestamp.toInstant());
                        } else if (a[index] instanceof Float) {
                            Float value = (Float) a[index];
                            BeanUtils.setProperty(summary, field, value);
                        } else {
                            BeanUtils.setProperty(summary, field, a[index]);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            list.add(summary);
        }
        return list;
    }

    private Map<String, Integer> getMapFields() {
        Map<String, Integer> map = new HashMap<>();
        int index = 0;
        map.put("id", index++);
        map.put("projectId", index++);
        map.put("poNo", index++);
        map.put("projectCode", index++);
        map.put("candidateCode", index++);
        map.put("resourceName", index++);
        map.put("task", index++);
        map.put("source", index++);
        map.put("target", index++);
        map.put("unitPrice", index++);
        map.put("ho", index++);
        map.put("hb", index++);
        map.put("total", index++);
        map.put("currency", index++);
        map.put("netOrHour", index++);
        map.put("reprep", index++);
        map.put("rep100", index++);
        map.put("rep99_95", index++);
        map.put("rep94_85", index++);
        map.put("rep84_75", index++);
        map.put("repnoMatch", index++);
        map.put("totalRep", index++);
        map.put("wrep", index++);
        map.put("w100", index++);
        map.put("w99_95", index++);
        map.put("w94_85", index++);
        map.put("w84_75", index++);
        map.put("wnoMatch", index++);
        map.put("createdAt", index++);
        map.put("updatedAt", index++);
        map.put("invoiceId", index++);
        map.put("company", index);

        return map;

    }

    private Map<String, String> getMapFilterFields() {
        Map<String, String> map = new HashMap<>();
        map.put("poNo", "code");
        map.put("projectCode", "code");
        map.put("candidateCode", "code");
        map.put("resourceName", "resource_name");
        map.put("task", "task");
        map.put("source", "source");
        map.put("target", "target");
        map.put("unitPrice", "unit_price");
        map.put("ho", "ho");
        map.put("hb", "hb");
        map.put("total", "total");
        map.put("currency", "currency");
        map.put("netOrHour", "net_or_hour");
        map.put("reprep", "reprep");
        map.put("rep100", "rep100");
        map.put("rep99_95", "rep99_95");
        map.put("rep94_85", "rep94_85");
        map.put("rep84_75", "rep84_75");
        map.put("repnoMatch", "repno_match");
        map.put("totalRep", "total_rep");
        map.put("wrep", "wrep");
        map.put("w100", "w100");
        map.put("w99_95", "w99_95");
        map.put("w94_85", "w94_85");
        map.put("w84_75", "w84_75");
        map.put("wnoMatch", "wno_match");
        map.put("createdAt", "created_at");
        map.put("updatedAt", "updated_at");
        map.put("progressStatus", "progress_status");
        map.put("finishedAt", "finished_at");
        map.put("invoiceId", "invoice_id");

        return map;

    }


    private String buildFilterCondition(PoFilterRequest filters) {
        if (filters.hasNoFilter()) {
            return null;
        }
        List<FilterRequest> rootFilters = filters.getRootFilters();
        List<FilterRequest> poFilters = filters.getPoFilters();
        List<FilterRequest> projectFilters = filters.getProjectFilters();
        List<FilterRequest> candidateFilters = filters.getCandidateFilters();
        StringBuilder builder = new StringBuilder();
        String[] partQueries = new String[4];
        partQueries[0] = buildSubFilterCondition(rootFilters, "a");
        partQueries[1] = buildSubFilterCondition(poFilters, "po");
        partQueries[2] = buildSubFilterCondition(projectFilters, "p");
        partQueries[3] = buildSubFilterCondition(candidateFilters, "c");

        for (String s : partQueries) {
            if (null == s) {
                continue;
            }
            builder.append(" AND ").append(s);
        }
        return builder.toString();
    }

    private String buildSubFilterCondition(List<FilterRequest> filters, String prefix) {
        if (null == filters || filters.isEmpty()) {
            return null;
        }
        Map<String, String> mapFilterFields = getMapFilterFields();
        StringBuilder builder = new StringBuilder();
        for (FilterRequest filter : filters) {
            if (null == filter) {
                continue;
            }
            String col = mapFilterFields.get(filter.getField());
            if ("resource_name".equals(col)) {
                builder.append("(a.external_resource = TRUE")
                        .append(" AND ").append("a.external_resource_name LIKE '%")
                        .append(filter.getValue())
                        .append("%' OR ").append("(a.external_resource is NULL OR a.external_resource = FALSE ) AND c.name LIKE '%").append(filter.getValue())
                        .append("%')");
                builder.append(" AND ");
                continue;
            }
            if (null != col) {
                builder.append(prefix).append(".").append(col);
            } else {
                builder.append(prefix).append(".").append(filter.getField());
            }
            if (filter.getValue() instanceof String) {
                builder.append(" like ").append("'%").append(filter.getValue()).append("%'");
            } else if (filter.getValue() instanceof Date) {
                try {
                    Date dateVal = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(filter.getValue()));
                    builder.append(" = ").append(dateVal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                builder.append(" = ").append(filter.getField());
            }
            builder.append(" AND ");
        }
        builder.append(" 1=1 ");
        return builder.toString();
    }
}
