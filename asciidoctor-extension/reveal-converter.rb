module Asciidoctor
  class Converter::RevealjsConverter < Converter::Base
    register_for 'revealjs'

    def initialize backend, opts = {}
      @backend = backend
      init_backend_traits basebackend: 'revealjs', filetype: 'html', outfilesuffix: '.html', supports_templates: true
    end

    def convert(node, transform = nil, opts = nil)
      # super
    end

  end
end
