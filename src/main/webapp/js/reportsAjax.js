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
	model.start();
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
	onChange = function(e) {
		var kc = e.keyCode;
		if (e.type === "keypress"
				|| (e.type === "keydown" && (kc === 8 || kc === 32 || kc === 13 || kc === 46))) {
			var elem = m(e.srcElement);
			var change = {
				id : elem.attr(attrName),
				value : elem.val(),
				timeStamp : e.timeStamp
			};
			model.changes(change);
		}
	};
	xns.all().keypress(function(e) {
		onChange(e);
	}).keydown(onChange);
}

var model = {
	clients : [],
	register : function(elem) {
		var id = elem.attr(attrName);
		var comment = elem.html();
		var iclt = new iClient(id, comment, new Date().getTime(),
				model.cooldown);
		iclt.listeners = [];
		iclt.listeners.push(model);
		this.clients.push(iclt);
	},
	// Recieves object change {id, value, timeStamp }
	changes : function(change) {
		var iClient = this.get2(change.id);
		iClient.update(change);
	},
	get2 : function(id) {
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
		var shadownew = "0 0 25px #44FF33, 0 0 10px 4px #77ff88";
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
				if (iclient.timeStamp > cli.timeStamp)
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
			if (iclient.update) {
				// object change {id, value, timeStamp } and boolean
				// cooldownOverride
				var change = {
					id : iclient.id,
					value : iclient.comment,
					timeStamp : iclient.timeStamp
				};
				iclient.update(change, true);
			}
		}
	},
	queueWithClientChanges : [],
	updatesPull : function() {
		// here I subtract 5 seconds to compensate latency, and other
		// circumstances
		// of course should be some better approach
		var iStamp = new Date().getTime() - 5000;
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
		var local = model.get2(iclient.id);
		if (local.updateFromServer && local.updateFromServer(iclient) === true) {
			var elem = m('[' + attrName + '=' + iclient.id + ']').first();
			elem.val(iclient.comment)
			var shadowold = "";
			var shadownew = "0 0 25px #ffc23f, 0 0 10px 4px #ff6e00";
			var wk = '-webkit-transition';
			var moz = '-moz-transition';
			var transitionnewwk = "all 3s cubic-bezier(1, .05, .60, .56)";
			var troldwk = "all 1s cubic-bezier(0, 0, .58, 1)";
			elem.css(wk, transitionnewwk).css(moz, transitionnewwk).css(
					'box-shadow', shadownew);
			setTimeout(function() {
				elem.css(wk, troldwk).css(moz, troldwk).css('box-shadow',
						shadowold);
			}, 4000);
		}
	}
};

// Initialized with model-clientid, comment text, timestamp, and the cooldown
// function
function iClient(id, comment, time, cooldown) {
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
	this.timeStamp = time;
	// Recieves object change {id, value, timeStamp } and boolean
	// cooldownOverride
	this.update = function(change, cooldownOverride) {
		if (change.timeStamp > this.timeStamp) {
			this.comment = change.value;
			this.timeStamp = change.timeStamp;

			if (this.cooldown() === false) {
				if (this.comment !== undefined) {
					if (cooldownOverride === undefined)
						this.cooldown(true);
					var url = m("#aj").attr("href");
					var data = {
						id : this.id,
						text : this.comment,
						time : this.timeStamp
					};
					var self = this;
					m.ajax({
						url : url,
						data : data
					}).done(function(data) {
						self.fireWritten();
					}).fail(function() {
						alert('Соединение не установлено');
					});
				}
			} else {
				this.fireCooldown();
			}
		}
	};
	this.updateFromServer = function(serverVersion) {
		if (serverVersion.id !== this.id)
			throw "Id does not match on update";
		if (serverVersion.timeStamp > this.timeStamp) {
			this.comment = serverVersion.comment;
			this.timeStamp = serverVersion.timeStamp;
			return true;
		}
		return false;
	}
}