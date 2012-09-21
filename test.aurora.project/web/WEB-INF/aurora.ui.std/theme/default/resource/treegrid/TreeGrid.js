/**
 * @class Aurora.TreeGrid
 * @extends Aurora.Grid
 * <p>树形表格组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object} config 配置对象. 
 */
$A.TreeGrid = Ext.extend($A.Grid, {
	initComponent : function(config) {
		$A.TreeGrid.superclass.initComponent.call(this, config);
		if (this.lockColumns.length > 0) {
			var sf = this;
			var ltid = sf.id + "_lb_tree"
			sf.lb.set({id : ltid});
			delete config.marginheight;
			delete config.marginwidth;
			var ltc = sf.createTreeConfig(config, sf.lockColumns, ltid, true,sf);
			sf.lockTree = new $A.Tree(ltc);
			sf.lb.addClass('item-treegrid');
			sf.lockTree.body = sf.lb;
			sf.lockTree.treegrid = sf;
			sf.lockTree.on('render', function() {
						sf.processData();
						Ext.DomHelper.insertHtml("beforeEnd", sf.lb.dom,
								'<div style="height:17px"></div>');
					}, sf)
			this.lockTree.on('expand', function(tree, node) {
						var node = this.unlockTree.getNodeById(node.id);
						node.expand();
					}, this);
			this.lockTree.on('collapse', function(tree, node) {
						var node = this.unlockTree.getNodeById(node.id);
						node.collapse();
					}, this)
		}

		var utid = this.id + "_ub_tree"
		this.ub.set({
					id : utid
				});
		var tc = this.createTreeConfig(config, this.unlockColumns, utid,this.lockColumns.length == 0, this);
		this.unlockTree = new $A.Tree(tc);
		this.ub.addClass('item-treegrid');
		this.unlockTree.body = this.ub;
		this.unlockTree.treegrid = this;
		this.unlockTree.on('render', this.processData, this)
	},
	initTemplate : function() {
		$A.TreeGrid.superclass.initTemplate.call(this);
		this.cbTpl = new Ext.Template('<center style="width:{width}px"><div class="{cellcls}" style="height:13px;padding:0px;" id="'
				+ this.id + '_{name}_{recordid}"></div></center>');
	},
	createTemplateData : function(col, record) {
		return {
			width : col.width - 2,
			// cwidth:col.width-4,
			recordid : record.id,
			visibility : col.hidden === true ? 'hidden' : 'visible',
			name : col.name
		}
	},
	createTreeConfig : function(config, columns, id, showSkeleton, grid) {
		var c = columns[0];
		var width = (c) ? c.width : 150;
		return Ext.apply(config, {
					sw : 20,
					id : id,
					showSkeleton : showSkeleton,
					width : width,
					column : c,
					displayfield : c.name,
					renderer : c.renderer,
					initColumns : function(node) {
						if (!node.isRoot()) {
							for (var i = 0; i < columns.length; i++) {
								var c = columns[i];
								if (c.name == node.ownerTree.displayfield)
									continue;
								var td = document.createElement('td');
								td['_type_'] = 'text';
								td['atype'] = 'grid-cell';
								td['dataindex'] = c.name;
								td['recordid'] = node.record.id;
								if (c.align)
									td.style.textAlign = c.align;
								node.els[c.name + '_td'] = td;

								// var div = document.createElement('div');
								// node.els[c.name+'_text']= div
								// Ext.fly(div).setWidth(c.width-4);
								// div.innerHTML =
								// grid.renderText(node.record,c,node.record.get(c.name));
								//                        

								var html = grid.createCell(c, node.record,
										false);
								var div = Ext.DomHelper.insertHtml(
										"afterBegin", td, html);
								Ext.fly(td).setWidth(c.width - 2);
								node.els[c.name + '_text'] = div;

								td.appendChild(node.els[c.name + '_text']);
								td.className = 'node-text';
								node.els['itemNodeTr']
										.appendChild(node.els[c.name + '_td']);
							}
						}
					},
					createTreeNode : function(item) {
						return new $A.Tree.TreeGridNode(item);
					},
					onNodeSelect : function(el) {
						el['itemNodeTable'].style.backgroundColor = '#dfeaf5';
					},
					onNodeUnSelect : function(el) {
						el['itemNodeTable'].style.backgroundColor = '';
					}
				});
	},
	processData : function(tree, root) {
		if (!root)
			return;
		var items = [];
		var datas = this.dataset.data;
		if (tree.showRoot) {
			this.processNode(items, root)
		} else {
			var children = root.children;
			for (var i = 0; i < children.length; i++) {
				this.processNode(items, children[i])
			}
		}
		this.dataset.data = items;
		// this.onLoad();
	},
	onLoad : function(){
        this.drawFootBar();
        $A.Masker.unmask(this.wb);
	},
	processNode : function(items, node) {
		items.add(node.record);
		var children = node.children;
		for (var i = 0; i < children.length; i++) {
			this.processNode(items, children[i])
		}
	},
	bind : function(ds) {
		if (typeof(ds) === 'string') {
			ds = $A.CmpManager.get(ds);
			if (!ds)
				return;
		}
		this.dataset = ds;
		this.processDataSetLiestener('on');
		if (this.lockTree)
			this.lockTree.bind(ds);
		this.unlockTree.bind(ds);
		this.drawFootBar();
	},
	setColumnSize : function(name, size) {
		$A.TreeGrid.superclass.setColumnSize.call(this, name, size);
		var c = this.findColByName(name);
		var tree = (c.lock == true) ? this.lockTree : this.unlockTree;
		c.width = size;
		if (name == tree.displayfield) tree.width = size;
		tree.root.setWidth(name, size);// (name == tree.displayfield) ? size-2
										// :
	},
	renderLockArea : function() {
		var v = 0;
		var columns = this.columns;
		for (var i = 0, l = columns.length; i < l; i++) {
			if (columns[i].lock === true) {
				;
				if (columns[i].hidden !== true)
					v += columns[i].width;
			}
		}
		this.lockWidth = v;
	},
	focusRow : function(row){
		var n=0,
			tree = this.unlockTree,
			hash = tree.nodeHash,
			datas = this.dataset.data;
        for(var i = 0 ; i<row ;i++){
        	if(tree.isAllParentExpand(hash[datas[i].id]))n++;
        }
        $A.TreeGrid.superclass.focusRow.call(this,n);
    },
	onMouseWheel : function(e){
    }
});
$A.Tree.TreeGridNode = Ext.extend($A.Tree.TreeNode, {
			createNode : function(item) {
				return new $A.Tree.TreeGridNode(item);
			},
			createCellEl : function(df) {
				var tree = this.getOwnerTree();
				var html = tree.treegrid.createCell(tree.column, this.record,
						false);
				var td = this.els[df + '_td'];
				var div = Ext.DomHelper.insertHtml("afterBegin", this.els[df
								+ '_td'], html);
				td['dataindex'] = df;
				td['atype'] = 'grid-cell';
				td['recordid'] = this.record.id;
				if (tree.column.align)
					td.style.textAlign = tree.column.align;
				this.els[df + '_text'] = div;
			},
			paintText : function() {
			},
			render : function() {
				$A.Tree.TreeGridNode.superclass.render.call(this);
				var tree = this.getOwnerTree();
				this.setWidth(tree.displayfield, tree.width);
			},
			setWidth : function(n, w) {
				$A.Tree.TreeGridNode.superclass.setWidth.call(this, n, w);
			}
		});