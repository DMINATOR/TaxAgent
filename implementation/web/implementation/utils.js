var sendAjaxRequest = function(action, data, callback, method){
	var request = { 
			action : action ,
			data : data
	};
	Ext.Ajax.request({
	   url: 'backend/engine.php',
	   success: callback,
	   failure: function(){
			alert ('unexpected error. please try again later');
	   },
	   method: Ext.isEmpty(method) ? 'POST' : method,
	   params: {
		json: JSON.stringify(request)
	   }
	});
}

var requestData = function(store, action, offset, itemsperpage, additionalparameters) {
	itemsperpage = itemsperpage || 10;
	offset = offset || 0;
	var request = { 
			action : action ,
			data : {
				ItemsPerPage: itemsperpage,
				ItemsOffset: offset
			}
	};
	if (!Ext.isEmpty(additionalparameters)) {
		Ext.applyIf(request.data, additionalparameters);
	}
	Ext.Ajax.request({
	   url: 'backend/engine.php',
	   success: function(response) {
			var json = Ext.util.JSON.decode(response.responseText);
			store.loadData(json.data);
	   },
	   failure: function(){
			alert ('unexpected error. please try again later');
	   },
	   method: 'POST',
	   params: {
		json: JSON.stringify(request)
	   }
	});
};

var createButton = function(text, onclick, cls, options){
	var btnOpts = {
		text: text,
		cls: cls,
		listeners: {
			click: onclick
		}
	};
	Ext.applyIf(btnOpts, options);

	return new Ext.Button(btnOpts);
}


var createLabel = function(text, cls, options){
	var lblOpts = {
		text: text,
		cls: cls,
		width: 400
	};
	Ext.applyIf(lblOpts, options);

	return new Ext.form.Label(lblOpts);
}

var createDateSelector = function(cls, options) {
	var fieldOptions = {
		cls: cls,
		format: "Y-m-d"
	};
	Ext.applyIf(fieldOptions, options);
	return new Ext.form.DateField(fieldOptions);
};

var createTextInput = function(text, cls, options) {
	var fieldOptions = {
		emptyText: text,
		cls: cls
	};
	Ext.applyIf(fieldOptions, options);
	return new Ext.form.TextField(fieldOptions);
};

var createComboBox = function(text, cls, options) {
	var fieldOptions = {
		emptyText: text,
		cls: cls
	};
	Ext.applyIf(fieldOptions, options);
	return new Ext.form.ComboBox(fieldOptions);
};

/*
var columnsExample = [
            {header: "ID", width: 60, dataIndex: 'ID', sortable: true},
            {header: "SubmitDate", width: 60, dataIndex: 'SubmitDate', sortable: true}
]

var jsonKeyExample = [
				{name: 'ID'},
				{name: 'SubmitDate'}        
			]
*/

var createDataGrid = function(action, columns, jsonKey, options, lazyLoad, additionalPanel, buttonIndex, getAdditionalParams, dblclick) {
	var offset = 0;
	var reader = new Ext.data.JsonReader(
			{
			},jsonKey
		);
	var store = new Ext.data.Store({
	  reader: reader
    });
	var gridPanel = new Ext.Panel({
		cls: 'gridPanel', 
		width: 'auto',
		height: 'auto'
	});
	var buttonsPanel = new Ext.Panel({
		height: 'auto', 
		width: (null != options && null != options.width) ? options.width : 'auto',
		layout: 'hbox'
	});
	var prevPageButton = createButton("<<", function(){
		offset -=10;
		var additionalParameters = null;
		if (!Ext.isEmpty(getAdditionalParams)) {
			additionalParameters = getAdditionalParams();
		}
		requestData(store, action, offset, 10, additionalParameters);
		if (0 == offset) {
			prevPageButton.disable();
		} else {
			prevPageButton.enable();
		}
	}, null, {
		disabled: true
	});
	var nextPageButton = createButton(">>", function(){
		offset +=10;
		var additionalParameters = null;
		if (!Ext.isEmpty(getAdditionalParams)) {
			additionalParameters = getAdditionalParams();
		}
		requestData(store, action, offset, 10, additionalParameters);
		if (0 != offset) {
			prevPageButton.enable();
		}
	});
	buttonsPanel.add(prevPageButton);
	buttonsPanel.add(nextPageButton);
	if (!Ext.isEmpty(additionalPanel)) {
		buttonsPanel.add(additionalPanel);
		if (!Ext.isEmpty(buttonIndex)){
			var additionalButton = additionalPanel.getComponent(buttonIndex);
			additionalButton.on('click', function(){
				offset = 0;
				prevPageButton.disable();
			})
		}
	}
	
	gridPanel.add(buttonsPanel);
		
	if (true != lazyLoad) {
		var addParams = null;
		if (!Ext.isEmpty(getAdditionalParams)) {
			addParams = getAdditionalParams()
		}
		requestData(store, action, null, null, addParams);
	}
	var gridOptions = {
        store: store,
        columns: columns,
        height:200,
		viewConfig: {
			forceFit: true
		}
    };
	
	Ext.applyIf(gridOptions, options);
	var grid = new Ext.grid.GridPanel(gridOptions);
	if (!Ext.isEmpty(dblclick)){
		grid.on('rowdblclick', dblclick);
	};
	gridPanel.add(grid);
	
	return [gridPanel, store];

};

var loadingMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});

var showload = function(){
	loadingMask.show();
}
var hideload = function(){
	loadingMask.hide();
}