/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 * 
 * Code generated by Microsoft (R) AutoRest Code Generator 0.11.0.0
 * Changes may cause incorrect behavior and will be lost if the code is
 * regenerated.
 */

'use strict';

/**
 * @class
 * Initializes a new instance of the A class.
 * @constructor
 * @member {string} [statusCode]
 * 
 */
function A(parameters) {
  if (parameters !== null && parameters !== undefined) {
    if (parameters.statusCode !== undefined) {
      this.statusCode = parameters.statusCode;
    }
  }    
}


/**
 * Validate the payload against the A schema
 *
 * @param {JSON} payload
 *
 */
A.prototype.serialize = function () {
  var payload = {};
  if (this['statusCode'] !== null && this['statusCode'] !== undefined) {
    if (typeof this['statusCode'].valueOf() !== 'string') {
      throw new Error('this[\'statusCode\'] must be of type string.');
    }
    payload['statusCode'] = this['statusCode'];
  }

  return payload;
};

/**
 * Deserialize the instance to A schema
 *
 * @param {JSON} instance
 *
 */
A.prototype.deserialize = function (instance) {
  if (instance) {
    if (instance['statusCode'] !== undefined) {
      this['statusCode'] = instance['statusCode'];
    }
  }

  return this;
};

module.exports = A;
