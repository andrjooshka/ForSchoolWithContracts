var m = jQuery;

//common names and functions
var xns = {
	all : function() {
		return m(xns.attrNameBoxed);
	},
    attrName:'model-id',
    attrNameBoxed: '[model-id]',
    fire:function(toFire){
        var lis;
        for(var i = 0; i < this.listeners.length;i++){
            lis = this.listeners[i];
            if(lis[toFire])
                lis[toFire].apply(lis, (Array.prototype.slice.call(arguments,1)));
        }
    },
	pushUrl : "",
	pushData : function(greg) {
		return {
			url : xns.pushUrl,
			data : {
				id : greg.id,
				comment : greg.comment,
				timeStamp : greg.timeStamp
			}
		};
	},
	pullUrl : "",
	pullData : function(timeStamp) {
		return {
			url : xns.pullUrl,
            type : "POST",
			data : {
				timeStamp : timeStamp
			}
		};
	},
	elem : function(iclientId) {
		return m('[' + attrName + '=' + iclientId + ']').first();
	}
};

// FIRE
m(go);

function go() {

	function resize() {
		m('div#payment textarea.cool').each(function() {
			this.style.height = (m(this).closest('td').height() - 8) + "px";
		});
	}

	function trim() {
		m('textarea.cool').each(function() {
			wr = m(this);
			wr.val(m.trim(wr.val()));
		});
	}

	function initNames() {
		xns.pushUrl = m("#aj").attr("href");
		xns.pullUrl = m("#ajpoll").attr("href");
	}

	function initModel() {
		// Iterate through all elements
		model.GregReg.get2 = function(idx) {
            for(var i = 0; i < model.GregReg.length ; i++)
                if (model.GregReg[i].id == idx)
                    return model.GregReg[i];
			throw "Index not found";
		};
		xns.all().each(function() {
			var elem = m(this);
			var alisa = new model.Alisa(elem);

			var id = alisa.getId();
			var ts = alisa.getTimeStamp();
			var cm = alisa.getComment();
			var greg = new model.Greg(id, ts, cm, [alisa, model.Daemon]);
			model.GregReg.push(greg);
		});
		model.Daemon.awakening();
	}

	function initHandlers() {
		var myTimeouts = [];
		// TODO check the caching
		myTimeouts.get2 = function(idx) {
			for (var i = 0; i < myTimeouts.length; i++) {
				if (myTimeouts[i] !== undefined && myTimeouts[i].id == idx)
					return myTimeouts[i];
			}
			return undefined;
		};

		onKey = function(e) {
			var kc = e.keyCode;
			if (e.type === "keypress" || (e.type === "keydown" && (kc === 8 || kc === 32 || kc === 13 || kc === 46))) {
				// this code now runs with the timeout of 200 milliseconds.
				// changes should be applied to the textarea first, then we could retrieve its value
				var elem = m(e.target);
				var elementId = elem.attr(xns.attrName);
				if (myTimeouts.get2(elementId))
					clearTimeout(myTimeouts.get2(elementId).timeoutId);

				myTimeouts.push({
					id : elementId,
					timeoutId : setTimeout(function() {
						// delete the key from the array
						for (var j = 0; j < myTimeouts.length; j++) {
							if (myTimeouts[j] && myTimeouts[j].id == elementId)
								delete myTimeouts[j];
						}

						// push change
						var temp = model.GregReg.get2(elementId);
						if (temp.onUpdate)
							temp.onUpdate({
								id : elementId,
								comment : elem.val(),
								timeStamp : new Date().getTime()
							});

					}, 200)
				});
			}
		};
		xns.all().keypress(function(e) {
			onKey(e);
		}).keydown(onKey);
	}

	var model = {
		// Company of good guys Gregs.
		// To be set up.
		// Each Greg should have predefined Alisa
		GregReg : [],
		// Underlying good guy Greg who is dealing with all updates
		Greg : function(id, timeStamp, comment, listeners) {
			this.id = id;
			this.timeStamp = timeStamp;
			this.comment = comment;
			this.listeners = listeners;
			this.onUpdate = function(greg) {
				//complete the updates function
				if (greg.timeStamp > this.timeStamp) {
					this.comment = greg.comment;
					this.timeStamp = greg.timeStamp;
					this.fireUpdate();
				}
			}
			this.onPushSuccess = function() {
                xns.fire.call(this, "onPushSuccess");
			}
			this.fireUpdate = function() {
				// TODO the only bad thing is updates will be sent to server twice.
				// if they came from server.
				xns.fire.call(this, "onUpdate", this);
			}
		},
		// Girl who abstracts textarea
		// textAreaElement is already wrapped jquery element
		// however we always could abstract this in some getter method
		// or just wrap these functions. Wrapping is the JS way.
		Alisa : function(textAreaElement) {
			this.teya = textAreaElement;
			// If Greg emits the update, Alisa should update the text and highlight the textArea
			this.onUpdate = function(greg) {
				// update, only if Gregs timeStamp is higher
				if (greg.timeStamp > this.getTimeStamp()) {
					this.setVal(greg.comment);
					this.setTimeStamp(greg.timeStamp);
					this.highlightJuicyFruit();
				}
			};

			this.onPushSuccess = function() {
				this.highlightEternalGreen();
			};

			this.setVal = function(value) {
				this.teya.val(value);
			};

			this.getId = function() {
				return this.teya.attr(xns.attrName);
			};
			this.getComment = function() {
				return this.teya.val();
			}
			this.getTimeStamp = function() {
				var ts = 0;
				try {
					ts = this.teya.attr('model-timestamp');
					if (!( ts instanceof Number))
						throw 'nan';
				} catch(e) {
					ts = 0;
				};
				return ts;
			};
			this.setTimeStamp = function(timeStamp) {
				this.teya.attr('model-timestamp', timeStamp);
			};
			this.highlightJuicyFruit = function() {
				var shadowold = "";
				var shadownew = "0 0 25px #ffc23f, 0 0 10px 4px #ff6e00";
				var wk = '-webkit-transition';
				var moz = '-moz-transition';
				var transitionnewwk = "all 2.5s cubic-bezier(1, .05, .60, .56)";
				var troldwk = "all .9s cubic-bezier(0, 0, .58, 1)";
				this.teya.css(wk, transitionnewwk).css(moz, transitionnewwk).css('box-shadow', shadownew);

				// cache the teya to make a closure
				var gizzelle = this.teya;
				setTimeout(function() {
					gizzelle.css(wk, troldwk).css(moz, troldwk).css('box-shadow', shadowold);
				}, 2700);
			};
			this.highlightEternalGreen = function() {
				var shadowold = "";
				var shadownew = "0 0 25px #44FF33, 0 0 10px 4px #77ff88";
				var wk = '-webkit-transition';
				var moz = '-moz-transition';
				var transitionnewwk = "all 2.5s cubic-bezier(1, .05, .60, .56)";
				var troldwk = "all .9s cubic-bezier(0, 0, .58, 1)";
				this.teya.css(wk, transitionnewwk).css(moz, transitionnewwk).css('box-shadow', shadownew);

				var gizzelle = this.teya;
				setTimeout(function() {
					gizzelle.css(wk, troldwk).css(moz, troldwk).css('box-shadow', shadowold);
				}, 2700);
			};
		},

		// Red eyed daemon, who checks pulls and pushes updates from and to server
		Daemon : {
			//
			awakening : function() {
				// Pull one time in five seconds
				setInterval(model.Daemon.pull, 5000);
				// Push function at first checks for availability of
				// updates, so it is cheap in resources
				setInterval(model.Daemon.push, 50);
			},
			pull : function() {
				m.ajax(xns.pullData(new Date().getTime() - 7000)).done(function(data) {
					if (data.updates) {
						for (var idx = 0; idx <  data.updates.length; idx++ ) {
							var up = data.updates[idx];
							// Deliver to the right Greg. This code is candidate for refactor
							var temp = model.GregReg.get2(up.id);
							if (temp.onUpdate)
								temp.onUpdate(up);
						}
					}
				});
			},
			pushcache : [],
			// dumb implementation. Just checks for the objects in the cache,
			// and if they exist -- pushes it to the client. One by one.
			push : function() {
				if (model.Daemon.pushcache.length == 0)
					return;
				var oneOfTheGregsToPush = model.Daemon.pushcache.shift();
				m.ajax(xns.pushData(oneOfTheGregsToPush)).done(function() {
					oneOfTheGregsToPush.onPushSuccess();
				});
			},
			// Daemon listens to Greg's publish, through this function
			onUpdate : function(greg) {
				//put the updated Greg in the cache

				// if comment is empty -- write 'null' to this object comment --
				// this will delete the comment from the database
				if (m.trim(greg.comment) == "")
					greg.comment = "null";
				model.Daemon.pushcache.push(greg);
			}
		}
	}

	resize();
	trim();
	initNames();
	initModel();
	initHandlers();
}

