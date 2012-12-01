var m = jQuery;
var attrName = 'model-clientid';
var attrNameBoxed = '[' + attrName + ']';
var xns = {
	all : function() {
		return m(attrNameBoxed);
	}
};

m(go);

function go() {
	resize();
	initClientModel();
	initEvents();
}

function resize() {
	m('div#payment textarea.cool').each(function() {
		this.style.height = (m(this).closest('td').height() - 8) + "px";
	});
}

function initClientModel() {
	xns.all().each(function() {
		model.register(m(this))
	});
}

function initEvents() {
	xns.all().keypress(function(e) {
		function f() {
			var elem = m(e.srcElement);
			model.changes(elem.attr(attrName), elem.val());
		}
		setTimeout(f, 300);
	});
	model.start();
}

var model = {
	clients : [],
	register : function(elem) {
		var id = elem.attr(attrName);
		var comment = elem.html();
		var iclt = new iClient(id, comment, model.cooldown);
		iclt.listeners = [];
		iclt.listeners.push(model);
		this.clients.push(iclt);
	},
	changes : function(id, newtext) {
		var iClient = this.get(id);
		iClient.update(newtext);
	},
	get : function(id) {
		for (idx in this.clients)
			if (this.clients[idx].id == id)
				return this.clients[idx];
		throw 'Element with id=' + id + ' not found';
	},
	cooldown : function(activate) {
		var onCooldown = false;

		if (activate)
			onCooldown = true;

		setTimeout(function() {
			onCooldown = false;
		}, 3000);
		return onCooldown;
	},
	onWritten : function(iclient) {
		var elem = m('[' + attrName + '=' + iclient.id + ']').first();
		var shadowold = "";
		var shadownew = "0 0 10px #44FF33, 0 0 5px 1px #77ff88";
		var wk = '-webkit-transition';
		var moz = '-moz-transition';
		var transitionnewwk = "all 3s cubic-bezier(1, .05, .60, .56)";
		var troldwk = "all 1s cubic-bezier(0, 0, .58, 1)";
		elem.css(wk, transitionnewwk).css(moz, transitionnewwk).css(
				'box-shadow', shadownew);
		setTimeout(function() {
			elem.css(wk, troldwk).css(moz, troldwk)
					.css('box-shadow', shadowold);
		}, 4000);
	},
	onCooldown : function(iclient) {
		for (idx in model.queueWithClientChanges) {
			cli = model.queueWithClientChanges[idx];
			if (cli.id === iclient.id) {
				model.queueWithClientChanges[idx] = iclient;
				return;
			}
		}
		model.queueWithClientChanges.push(iclient);
	},
	start : function() {
		setInterval(model.updatesPush, 3000);
		setInterval(model.updatesPull, 3000);
	},
	updatesPush : function() {
		while (model.queueWithClientChanges.length > 0) {
			var iclient = model.queueWithClientChanges.shift();
			if (iclient.update)
				iclient.update(iclient.comment, true);
		}
	},
	queueWithClientChanges : [],
	updatesPull : function() {
		var iStamp = new Date().getTime();
		var iurl = m("#ajpoll").attr("href");
		rSettings = {
			url : iurl,
			data : {
				timestamp : iStamp
			}
		}
		m.ajax(rSettings).done(function(data) {
			if (data)
				if (data.updates) {
					for (idx in data.updates)
						model.clientUpdate(data.updates[idx]);
				}
		}).fail(function() {
			alert('fail');
		});
	},
	clientUpdate : function(iclient) {
		var elem = m('[' + attrName + '=' + iclient.id + ']').first();
		elem.val(iclient.comment)
		var shadowold = "";
		var shadownew = "0 0 25px #ffc23f, 0 0 10px 4px #ff6e00";
		var wk = '-webkit-transition';
		var moz = '-moz-transition';
		var transitionnewwk = "all 1s cubic-bezier(1, .05, .60, .56)";
		var troldwk = "all 1s cubic-bezier(0, 0, .58, 1)";
		elem.css(wk, transitionnewwk).css(moz, transitionnewwk).css(
				'box-shadow', shadownew);
		setTimeout(function() {
			elem.css(wk, troldwk).css(moz, troldwk)
					.css('box-shadow', shadowold);
		}, 4000);
	}
};

// Initialized with model-clientid, comment text, and the cooldown function
function iClient(id, comment, cooldown) {
	this.fireWritten = function() {
		if (this.listeners)
			for (idx in this.listeners) {
				var listener = this.listeners[idx];
				if (listener.onWritten)
					listener.onWritten(this);
			}
	};
	this.fireCooldown = function() {
		if (this.listeners)
			for (idx in this.listeners) {
				var listener = this.listeners[idx];
				if (listener.onCooldown)
					listener.onCooldown(this);
			}
	};

	this.cooldown = cooldown;
	this.id = id;
	this.comment = comment;
	this.update = function(text, cooldownOverride) {
		this.comment = text;
		if (!this.cooldown()) {
			if (this.comment !== undefined && this.comment.length > 2) {
				if (cooldownOverride === undefined)
					this.cooldown(true);
				var url = m("#aj").attr("href");
				var data = {
					id : this.id,
					text : this.comment
				};
				var self = this;
				m.ajax({
					url : url,
					data : data
				}).done(function(data) {
					self.fireWritten();
				}).fail(function() {
					alert('fail');
				});
			}
		} else {
			this.fireCooldown();
		}
	};
}