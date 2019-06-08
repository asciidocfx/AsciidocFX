module Asciidoctor
  class Converter::DeckjsConverter < Converter::Base
    register_for 'deckjs'

    def initialize backend, opts = {}
      @backend = backend
      init_backend_traits basebackend: 'deckjs', filetype: 'html', outfilesuffix: '.html', supports_templates: true
    end

    def convert(node, transform = nil, opts = nil)
      # super
    end

  end
end
