package com.kayode.lostNfound.lazymodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kayode.lostNfound.constants.QueryType;
import com.kayode.lostNfound.model.Item;
import com.kayode.lostNfound.model.ItemType;
import com.kayode.lostNfound.model.PagedList;
import com.kayode.lostNfound.service.ItemService;

public class ItemLazyDataModel extends LazyDataModel<Item> {

    private static final Logger LOG = LoggerFactory.getLogger(ItemLazyDataModel.class);

    private final ItemService service;
    private final QueryType query;
    private final String searchTerm;

    private List<Item> list = new ArrayList<>();

    public ItemLazyDataModel(ItemService service, QueryType query) {
        this(service, query, null);
    }

    public ItemLazyDataModel(ItemService service, QueryType query, String searchTerm) {
        this.service    = service;
        this.query      = query;
        this.searchTerm = (searchTerm != null && !searchTerm.trim().isEmpty()) ? searchTerm.trim() : null;
    }

    @Override
    public Item getRowData(String rowKey) {
        for (Item r : list) {
            if (String.valueOf(r.getId()).equals(rowKey)) return r;
        }
        return null;
    }

    @Override
    public Object getRowKey(Item r) {
        return r.getId();
    }

    @Override
    public List<Item> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {
        try {
            PagedList<Item> pagedList;
            switch (query) {
                case GET_ALL_ITEM:
                    pagedList = service.fetchItem(first, pageSize);
                    break;
                case GET_LOST:
                    pagedList = service.fetchItem(first, pageSize, ItemType.LOST, searchTerm);
                    break;
                case GET_FOUND:
                    pagedList = service.fetchItem(first, pageSize, ItemType.FOUND, searchTerm);
                    break;
                default:
                    LOG.warn("Unknown query type: {}", query);
                    pagedList = new PagedList<>();
                    pagedList.setList(new ArrayList<>());
                    pagedList.setCount(0);
            }
            this.setRowCount(pagedList.getCount());
            list = pagedList.getList();
            return list;
        } catch (Exception e) {
            LOG.error("Error paginating items", e);
            return new ArrayList<>();
        }
    }
}
