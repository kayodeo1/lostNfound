/**
 * 
 */
package com.kayode.lostNfound.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.io.FileUtils;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.crypto.hash.Sha256Hash;
import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kayode.lostNfound.constants.QueryType;
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.ItemStatus;
import com.kayode.lostNfound.model.ItemType;
import com.kayode.lostNfound.model.Media;
import com.kayode.lostNfound.model.MediaType;
import com.kayode.lostNfound.model.PagedList;

/**
 * @author Kayode Ojo
 *
 */
@Stateless
public class ItemService {

	@PersistenceContext(unitName = "app")
	private EntityManager em;
	
	@Inject MediaService mediaService;


	private static Logger LOG = LoggerFactory.getLogger(ItemService.class);

	public void createItem(Item e){
		em.persist(e);
		em.flush();
	}
	public void createItem(Item e,Media m){
		em.persist(e);
		em.flush();
		m.setItemID(e.getId());
		mediaService.createMedia(m);
	}

	public Item updateItem(Item e){
		return em.merge(e);
	}
	
	public Item findItem(Long id){
		return em.find(Item.class, id);
	}
	
	public PagedList<Item> fetchItem(int first, int pageSize){
		PagedList<Item> list = new PagedList<Item>();
		TypedQuery<Item> query = em.createQuery(
				"select s from Item s WHERE s.itemStatus !=:status order by s.createdDate desc",
				Item.class);
		query.setParameter("status", ItemStatus.RESOLVED);
		query.setFirstResult(first).setMaxResults(pageSize);
		List<Item> res = query.getResultList();
		list.setList(res);

		Number count = fetchItemCount();
		list.setCount(count.intValue());

		return list;
	}
	
		public Number fetchItemCount()
			{
		TypedQuery<Number> query = em.createQuery(
				"select count(s.id) from Item s WHERE s.itemStatus !=:status",
				Number.class);
		query.setParameter("status", ItemStatus.RESOLVED);

		Number res = query.getSingleResult();
		return res;
	}
		public PagedList<Item> fetchItem(int first, int pageSize,ItemType m){
			PagedList<Item> list = new PagedList<Item>();
			TypedQuery<Item> query = em.createQuery(
					"select s from Item s WHERE s.itemType =:m order by s.createdDate desc",
					Item.class);
			query.setParameter("m", m);
			query.setFirstResult(first).setMaxResults(pageSize);
			List<Item> res = query.getResultList();
			list.setList(res);

			Number count = fetchItemCount(m);
			list.setCount(count.intValue());

			return list;
		}
		
			public Number fetchItemCount(ItemType m)
				{
			TypedQuery<Number> query = em.createQuery(
					"select count(s.id) from Item s WHERE s.itemType =:m",
					Number.class);
			query.setParameter("m", m);
			Number res = query.getSingleResult();
			return res;
		}

}
