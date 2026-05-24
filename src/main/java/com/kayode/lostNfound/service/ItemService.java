package com.kayode.lostNfound.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.ItemStatus;
import com.kayode.lostNfound.model.ItemType;
import com.kayode.lostNfound.model.Media;
import com.kayode.lostNfound.model.PagedList;

@Stateless
public class ItemService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemService.class);

    @PersistenceContext(unitName = "app")
    private EntityManager em;

    @Inject
    private MediaService mediaService;

    public void createItem(Item e) {
        em.persist(e);
        em.flush();
    }

    public void createItem(Item e, Media m) {
        em.persist(e);
        em.flush();
        m.setItemID(e.getId());
        mediaService.createMedia(m);
    }

    public Item updateItem(Item e) {
        return em.merge(e);
    }

    public Item findItem(Long id) {
        return em.find(Item.class, id);
    }

    public void deleteItem(Long id) {
        Item item = em.find(Item.class, id);
        if (item != null) em.remove(item);
    }

    // All active items (excludes RESOLVED), no search
    public PagedList<Item> fetchItem(int first, int pageSize) {
        TypedQuery<Item> q = em.createQuery(
            "SELECT s FROM Item s WHERE s.itemStatus != :resolved ORDER BY s.createdDate DESC",
            Item.class);
        q.setParameter("resolved", ItemStatus.RESOLVED);
        q.setFirstResult(first).setMaxResults(pageSize);

        TypedQuery<Number> cq = em.createQuery(
            "SELECT COUNT(s.id) FROM Item s WHERE s.itemStatus != :resolved", Number.class);
        cq.setParameter("resolved", ItemStatus.RESOLVED);

        PagedList<Item> list = new PagedList<>();
        list.setList(q.getResultList());
        list.setCount(cq.getSingleResult().intValue());
        return list;
    }

    // Items by type, excludes RESOLVED, with optional search term
    public PagedList<Item> fetchItem(int first, int pageSize, ItemType type, String search) {
        if (search == null || search.trim().isEmpty()) {
            return fetchItem(first, pageSize, type);
        }
        String like = "%" + search.toLowerCase() + "%";
        TypedQuery<Item> q = em.createQuery(
            "SELECT s FROM Item s WHERE s.itemType = :type AND s.itemStatus != :resolved " +
            "AND (LOWER(s.name) LIKE :s OR LOWER(s.description) LIKE :s OR LOWER(s.location) LIKE :s) " +
            "ORDER BY s.createdDate DESC", Item.class);
        q.setParameter("type", type);
        q.setParameter("resolved", ItemStatus.RESOLVED);
        q.setParameter("s", like);
        q.setFirstResult(first).setMaxResults(pageSize);

        TypedQuery<Number> cq = em.createQuery(
            "SELECT COUNT(s.id) FROM Item s WHERE s.itemType = :type AND s.itemStatus != :resolved " +
            "AND (LOWER(s.name) LIKE :s OR LOWER(s.description) LIKE :s OR LOWER(s.location) LIKE :s)",
            Number.class);
        cq.setParameter("type", type);
        cq.setParameter("resolved", ItemStatus.RESOLVED);
        cq.setParameter("s", like);

        PagedList<Item> list = new PagedList<>();
        list.setList(q.getResultList());
        list.setCount(cq.getSingleResult().intValue());
        return list;
    }

    // Items by type, excludes RESOLVED, no search
    public PagedList<Item> fetchItem(int first, int pageSize, ItemType type) {
        TypedQuery<Item> q = em.createQuery(
            "SELECT s FROM Item s WHERE s.itemType = :type AND s.itemStatus != :resolved ORDER BY s.createdDate DESC",
            Item.class);
        q.setParameter("type", type);
        q.setParameter("resolved", ItemStatus.RESOLVED);
        q.setFirstResult(first).setMaxResults(pageSize);

        TypedQuery<Number> cq = em.createQuery(
            "SELECT COUNT(s.id) FROM Item s WHERE s.itemType = :type AND s.itemStatus != :resolved",
            Number.class);
        cq.setParameter("type", type);
        cq.setParameter("resolved", ItemStatus.RESOLVED);

        PagedList<Item> list = new PagedList<>();
        list.setList(q.getResultList());
        list.setCount(cq.getSingleResult().intValue());
        return list;
    }

    // Count for admin dashboard — distinct counts by type (all statuses)
    public Number fetchItemCount(ItemType type) {
        TypedQuery<Number> q = em.createQuery(
            "SELECT COUNT(s.id) FROM Item s WHERE s.itemType = :type", Number.class);
        q.setParameter("type", type);
        return q.getSingleResult();
    }

    // Items reported by a specific user (for My Activity)
    public List<Item> fetchItemsByUserId(Long userId) {
        return em.createQuery(
            "SELECT s FROM Item s WHERE s.userId = :uid ORDER BY s.createdDate DESC", Item.class)
            .setParameter("uid", userId)
            .getResultList();
    }
}
