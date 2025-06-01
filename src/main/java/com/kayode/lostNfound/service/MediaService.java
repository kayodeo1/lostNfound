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
import com.kayode.lostNfound.model.Media;
import com.kayode.lostNfound.model.PagedList;

/**
 * @author AAfolayan
 *
 */
@Stateless
public class MediaService {

	@PersistenceContext(unitName = "app")
	private EntityManager em;

	private static Logger LOG = LoggerFactory.getLogger(MediaService.class);

	public void createMedia(Media e) {
		em.persist(e);
		em.flush();
	}

	public Media updateMedia(Media e) {
		return em.merge(e);
	}

	public Media findMedia(Long id) {
		return em.find(Media.class, id);
	}

	public PagedList<Media> fetchMedia(int first, int pageSize) {
		PagedList<Media> list = new PagedList<Media>();
		TypedQuery<Media> query = em.createQuery("select s from Media s order by s.createdDate desc", Media.class);
		query.setFirstResult(first).setMaxResults(pageSize);
		List<Media> res = query.getResultList();
		list.setList(res);

		Number count = fetchMediaCount();
		list.setCount(count.intValue());

		return list;
	}

	public Number fetchMediaCount() {
		TypedQuery<Number> query = em.createQuery("select count(s.id) from Media s", Number.class);
		Number res = query.getSingleResult();
		return res;
	}

	public Media findMediaForContent(Long id) {
		TypedQuery<Media> query = em.createQuery("select s from Media s WHERE  s.itemID =:id", Media.class);
		query.setParameter("id", id);
		try {
			Media result = query.getSingleResult();
			return result;
		} catch (Exception e) {
			
			return null;

		}

	}

}
