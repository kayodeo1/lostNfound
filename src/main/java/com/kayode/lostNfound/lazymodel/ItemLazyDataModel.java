/**
 * 
 */
package com.kayode.lostNfound.lazymodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kayode.lostNfound.constants.QueryType;
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.PagedList;
import com.kayode.lostNfound.service.ItemService;

/**
 * @author Kayode Ojo
 *
 */
public class ItemLazyDataModel extends LazyDataModel<Item> {

	private ItemService service;
	private QueryType query;

	List<Item> list = new ArrayList<>();

	private static Logger LOG = LoggerFactory.getLogger(ItemLazyDataModel.class);

	public ItemLazyDataModel(ItemService service,QueryType query) {
		this.service = service;
		this.query = query;
	}

	@Override
	public Item getRowData(String rowKey) {
		// LOG.info("getRowData method invoked!");
		for (Item r : list) {
			if ((r.getId()).equals(rowKey))
				return r;
		}

		return null;
	}

	@Override
	public Object getRowKey(Item r) {
		// LOG.info("getRowKey method invoked! " + cdt);
		return r.getId();
	}

	@Override
	public List<Item> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		try {
			LOG.info("query invoked >>> " + query);
			List<Item> data = new ArrayList<>();
			// paginate db entries
			PagedList<Item> pagedList = new PagedList<>();
			switch (query) {
			case GET_ALL_ITEM:
				pagedList = service.fetchItem(first, pageSize);
				break;
			default:
				LOG.warn("query type not found! , " + query);
				break;
			}

			// rowCount
			int dataSize = pagedList.getCount();// data.size();
			this.setRowCount(dataSize);

			// LOG.info("count >>> " + dataSize + " , pagedList.getList() >>> "
			// + pagedList.getList().size());

			return pagedList.getList();

		} catch (Exception e) {
			LOG.error("oops error encountered while paginating Item entries!!!", e);
			e.printStackTrace();
			return new ArrayList<Item>();
		}
	}

}
